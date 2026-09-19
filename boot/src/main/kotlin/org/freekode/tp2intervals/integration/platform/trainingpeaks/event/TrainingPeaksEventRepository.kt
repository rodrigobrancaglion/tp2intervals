package org.freekode.tp2intervals.integration.platform.trainingpeaks.event

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.event.Event
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.provider.event.IEventRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TrainingPeaksEventRepository(
    private val trainingPeaksEventApiClient: TrainingPeaksEventApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
) : IEventRepository {

    private val logger = AppLogger.get(this.javaClass)

    override fun platform() = Platform.TRAINING_PEAKS

    /**
     * Fetches all events (races) from TP for the given date range.
     * Conversion is delegated to TPToEventConverter.
     */
    override fun getEvents(startDate: LocalDate, endDate: LocalDate): List<Event> {
        val userId = trainingPeaksUserRepository.getUser().userId
        val tpEvents = trainingPeaksEventApiClient.getEvents(userId, startDate.toString(), endDate.toString())
        logger.infoL3In("Found ${tpEvents.size} TP events in $startDate..$endDate")
        return tpEvents.mapNotNull { TPToEventConverter.convert(it) }
    }

    override fun saveEvents(events: List<Event>) {
        if (events.isEmpty()) return

        val user = trainingPeaksUserRepository.getUser()
        val userId = user.userId

        // Determine date range to fetch existing events for deduplication
        val minDate = events.minOf { it.date }
        val maxDate = events.maxOf { it.date }

        val existingIcuIds = trainingPeaksEventApiClient.getEvents(userId, minDate.toString(), maxDate.toString())
            .filter { it.externalEventSource == "Intervals" }
            .mapNotNull { it.externalEventId }
            .toSet()

        logger.infoL3In("Existing TP events with Intervals IDs in range: ${existingIcuIds.size}")

        val newEvents = events.filter { it.externalId !in existingIcuIds }
        val skipped = events.size - newEvents.size

        if (skipped > 0) {
            logger.infoL3In("Skipping $skipped already synced TP event(s)")
        }

        if (newEvents.isEmpty()) {
            logger.infoL3In("No new events to save to TP")
            return
        }

        logger.infoL3In("Saving ${newEvents.size} new event(s) to TP for user $userId")

        newEvents.forEach { event ->
            try {
                val tpDto = EventToTPConverter.convert(event, userId.toLongOrNull())
                logger.infoL3In("Saving event '${event.name}' to TP for user $userId")
                trainingPeaksEventApiClient.createEvent(userId, tpDto)
            } catch (e: Exception) {
                logger.errorL3In("Error saving event '${event.name}' to TP", e)
            }
        }
        logger.infoL3In("Finished saving events to TP")
    }
}
