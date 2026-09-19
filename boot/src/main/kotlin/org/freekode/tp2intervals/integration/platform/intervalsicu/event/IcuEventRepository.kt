package org.freekode.tp2intervals.integration.platform.intervalsicu.event

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.*
import org.freekode.tp2intervals.domain.event.Event
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IcuWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.event.IEventRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Repository
class IcuEventRepository(
    private val icuWorkoutApiClient: IcuWorkoutApiClient,
    private val icuConfigurationRepository: IcuConfigurationRepository,
) : IEventRepository {

    private val logger = AppLogger.get(this.javaClass)

    override fun platform() = Platform.INTERVALS

    /**
     * Returns all race events (RACE_A/B/C) from ICU for the given date range.
     * GET /api/v1/athlete/{athleteId}/events?oldest=...&newest=...&resolve=true
     */
    override fun getEvents(startDate: LocalDate, endDate: LocalDate): List<Event> {
        val config = icuConfigurationRepository.getConfiguration()
        val icuEvents = icuWorkoutApiClient.getEvents(
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
                val startDateLocal = LocalDate.parse(dto.start_date_local, DateTimeFormatter.BASIC_ISO_DATE)

                Event(
                    externalId = dto.id.toString(),
                    name = dto.name,
                    date = startDateLocal,
                    category = cat,
                    eventType = resolveEventType(dto.getTrainingType().title),
                    subEventType = if (dto.getTrainingType().title.lowercase() == "mtb") TrainingType.MTB.title else null,
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
    override fun saveEvents(events: List<Event>) {
        if (events.isEmpty()) return

        val config = icuConfigurationRepository.getConfiguration()
        val athleteId = config.athleteId

        // Determine date range from the incoming events
        val minDate = events.minOf { it.date }
        val maxDate = events.maxOf { it.date }

        // Fetch existing ICU events in that range to detect duplicates
        val existingTpIds = icuWorkoutApiClient
            .getEvents(athleteId, minDate.toString(), maxDate.toString(),
                config.powerRange, config.hrRange, config.paceRange)
            .filter { it.category.startsWith("RACE") }
            .mapNotNull { dto ->
                dto.description?.let {
                    ExternalData.empty().fromSimpleString(it).trainingPeaksId
                }
            }
            .toSet()

        logger.infoL3In("Existing ICU events with TP ids in range: ${existingTpIds.size}")

        val newEvents = events.filter { it.externalId !in existingTpIds }
        val skipped = events.size - newEvents.size

        if (skipped > 0) {
            logger.infoL3In("Skipping $skipped already synced event(s)")
        }
        if (newEvents.isEmpty()) {
            logger.infoL3In("No new events to save")
            return
        }

        logger.infoL3In("Saving ${newEvents.size} new event(s) to ICU athlete $athleteId")

        val requests = newEvents.map { event ->
            val fullDescription = ExternalData.empty().withTrainingPeaks(event.externalId).buildDescription(event.description)
            IcuEventEx(
                name = event.name,
                description = fullDescription,
                start_date_local = "${event.date}T00:00:00",
                category = event.category.name,   // RACE_A, RACE_B, RACE_C
                type = resolveIcuType(event),
                moving_time = event.durationSeconds,
                distance = event.distance,
                icu_training_load = event.tss
            )
        }

        icuWorkoutApiClient.createEventsBulk(athleteId, requests)
        logger.infoL3In("Saved ${requests.size} event(s) to ICU")
    }

    private fun resolveCategory(category: String) = when (category) {
        "RACE_A" -> OtherType.RACE_A
        "RACE_B" -> OtherType.RACE_B
        "RACE_C" -> OtherType.RACE_C
        else -> null
    }

    private fun resolveIcuType(event: Event): String = when (event.eventType) {
        EventType.CYCLING    -> TrainingType.BIKE.title
        EventType.RUNNING    -> TrainingType.RUN.title
        EventType.SWIMMING   -> TrainingType.SWIM.title
        EventType.MULTISPORT -> "Triathlon"
        EventType.ROWING     -> "Row"
        else -> EventType.OTHER.title  // Default to "Other" if type unknown
    }

    private fun resolveEventType(type: String) = when (type.uppercase()) {
        TrainingType.BIKE.title.uppercase() -> EventType.CYCLING
        TrainingType.MTB.title.uppercase()  -> EventType.CYCLING
        TrainingType.RUN.title.uppercase()  -> EventType.RUNNING
        TrainingType.SWIM.title.uppercase() -> EventType.SWIMMING
        "triathlon" -> EventType.MULTISPORT
        "row" -> EventType.ROWING
        else -> null
    }
}
