package org.freekode.tp2intervals.integration.platform.intervalsicu.wellness

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IntervalsWellnessRepository(
    private val intervalsWellnessApiClient: IntervalsWellnessApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
) : IWellnessRepository {

    override fun platform() = Platform.INTERVALS

    override fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness> {
        val configuration = intervalsConfigurationRepository.getConfiguration()

        val listWellnessDTO = intervalsWellnessApiClient.getWellness(
            configuration.athleteId,
            startDate.toString(),
            endDate.toString())

        val listWellness = listWellnessDTO
            .map { IntervalsWellnessConverter(it).toDomain() }

        return listWellness
    }

    /**
     * Orchestrates the synchronization of wellness data for a given date range.
     */
    override fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId

        // Create a map for quick lookup of existing data by date
        val wellnessMap = wellnesses.filterNotNull().associateBy { it.date?.take(10) ?: "" }

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val dateStr = currentDate.toString()
            val wellnessEntry = wellnessMap[dateStr]

            if (wellnessEntry != null) {
                // Day has data: Perform update
                updateWellness(athleteId, wellnessEntry)
            } else {
                // Day is empty: Perform clear
                clearWellness(athleteId, dateStr)
            }

            currentDate = currentDate.plusDays(1)
        }
    }

    /**
     * Sends a filled DTO to update wellness metrics for a specific date.
     */
    private fun updateWellness(athleteId: String, wellness: Wellness) {
        val requestDTO = IntervalsWellnessConverter(wellness).toDTO()
        intervalsWellnessApiClient.updateWellness(athleteId, requestDTO)
    }

    /**
     * Sends an empty DTO to reset/clear all wellness metrics for a specific date.
     */
    private fun clearWellness(athleteId: String, date: String) {
        val emptyWellness = IntervalsWellnessDTO(id = date)
        intervalsWellnessApiClient.updateWellness(athleteId, emptyWellness)
    }

}
