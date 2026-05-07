package org.freekode.tp2intervals.integration.platform.intervalsicu.event

import org.freekode.tp2intervals.aspect.LogRepository
import org.freekode.tp2intervals.domain.*
import org.freekode.tp2intervals.domain.event.Event
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventRequestDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IntervalsWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.event.IEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IntervalsEventRepository(
    private val intervalsWorkoutApiClient: IntervalsWorkoutApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
) : IEventRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun platform() = Platform.INTERVALS

    /**
     * Returns all race events (RACE_A/B/C) from ICU for the given date range.
     * GET /api/v1/athlete/{athleteId}/events?oldest=...&newest=...&resolve=true
     */
    @LogRepository
    override fun getEvents(startDate: LocalDate, endDate: LocalDate): List<Event> {
        val config = intervalsConfigurationRepository.getConfiguration()
        val icuEvents = intervalsWorkoutApiClient.getEvents(
            config.athleteId,
            startDate.toString(),
            endDate.toString(),
            config.powerRange,
            config.hrRange,
            config.paceRange,
        )
        return icuEvents
            .filter { it.category.startsWith("RACE") }
            .mapNotNull { dto ->
                val cat = resolveCategory(dto.category) ?: return@mapNotNull null
                Event(
                    externalId = dto.id.toString(),
                    name = dto.name,
                    date = dto.start_date_local.toLocalDate(),
                    category = cat,
                    eventType = dto.type?.let { resolveEventType(it) },
                    subEventType = if (dto.type?.lowercase() == "mtb") "MTB" else null,
                    description = dto.description,
                    durationSeconds = dto.moving_time,
                    distance = dto.distance,
                    tss = dto.icu_training_load,
                )
            }
    }

    /**
     * Saves new events to ICU, skipping duplicates.
     * Deduplication: before saving, fetches existing ICU events in the same date range
     * and extracts trainingPeaksId from each event's description (ExternalData format).
     * Any incoming event whose externalId is already present in ICU is skipped.
     *
     * The description written to ICU includes the ExternalData block:
     *   //////////
     *   trainingPeaksId=<id>
     * This mirrors the same mechanism used for Workouts.
     */
    @LogRepository
    override fun saveEvents(events: List<Event>) {
        if (events.isEmpty()) return

        val config = intervalsConfigurationRepository.getConfiguration()
        val athleteId = config.athleteId

        // Determine date range from the incoming events
        val minDate = events.minOf { it.date }
        val maxDate = events.maxOf { it.date }

        // Fetch existing ICU events in that range to detect duplicates
        val existingTpIds = intervalsWorkoutApiClient
            .getEvents(athleteId, minDate.toString(), maxDate.toString(),
                config.powerRange, config.hrRange, config.paceRange)
            .filter { it.category.startsWith("RACE") }
            .mapNotNull { dto ->
                dto.description?.let {
                    ExternalData.empty().fromSimpleString(it).trainingPeaksId
                }
            }
            .toSet()

        log.info("Existing ICU events with TP ids in range: ${existingTpIds.size}")

        val newEvents = events.filter { it.externalId !in existingTpIds }
        val skipped = events.size - newEvents.size

        if (skipped > 0) {
            log.info("Skipping $skipped already synced event(s)")
        }
        if (newEvents.isEmpty()) {
            log.info("No new events to save")
            return
        }

        log.info("Saving ${newEvents.size} new event(s) to ICU athlete $athleteId")

        val requests = newEvents.map { event ->
            val fullDescription = ExternalData.empty().withTrainingPeaks(event.externalId).buildDescription(event.description)
            IcuEventRequestDTO(
                category = event.category.name,   // RACE_A, RACE_B, RACE_C
                name = event.name,
                description = fullDescription,
                type = resolveIcuType(event),
                moving_time = event.durationSeconds,
                distance = event.distance,
                start_date_local = "${event.date}T00:00:00",
                entered = false,
                indoor = false,
                icu_training_load = event.tss,
            )
        }

        intervalsWorkoutApiClient.createEventsBulk(athleteId, requests)
        log.info("Saved ${requests.size} event(s) to ICU")
    }

    private fun resolveCategory(category: String) = when (category) {
        "RACE_A" -> OtherType.RACE_A
        "RACE_B" -> OtherType.RACE_B
        "RACE_C" -> OtherType.RACE_C
        else -> null
    }

    private fun resolveIcuType(event: Event): String = when (event.eventType) {
        EventType.CYCLING -> TrainingType.BIKE.title
        EventType.RUNNING -> TrainingType.RUN.title
        EventType.SWIMMING -> "Swim"
        EventType.MULTISPORT -> "Triathlon"
        EventType.ROWING -> "Row"
        else -> "Other"  // Default to "Other" if type unknown
    }

    private fun resolveEventType(type: String) = when (type.lowercase()) {
        "ride" -> EventType.CYCLING
        "mtb" -> EventType.CYCLING
        "run" -> EventType.RUNNING
        "swim" -> EventType.SWIMMING
        "triathlon" -> EventType.MULTISPORT
        "row" -> EventType.ROWING
        else -> null
    }
}
