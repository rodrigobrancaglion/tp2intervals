package org.freekode.tp2intervals.integration.platform.intervalsicu.wellness

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.wellness.dto.IcuWellness
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IcuWellnessRepository(
    private val icuWellnessApiClient: IcuWellnessApiClient,
    private val icuConfigurationRepository: IcuConfigurationRepository,
) : IWellnessRepository {

    override fun platform() = Platform.INTERVALS

    override fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness> {
        val configuration = icuConfigurationRepository.getConfiguration()

        val listWellnessDTO = icuWellnessApiClient.getWellness(
            configuration.athleteId,
            startDate.toString(),
            endDate.toString())

        val listWellness = listWellnessDTO
            .map { IcuWellnessConverter(it).toDomain() }

        return listWellness
    }

    /**
     * Orchestrates the synchronization of wellness data for a given date range.
     */
    override fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate) {
        val athleteId = icuConfigurationRepository.getConfiguration().athleteId

        wellnesses.filterNotNull().forEach { wellness ->
            val wellnessDate = LocalDate.parse(wellness.date)
            if (!wellnessDate.isBefore(startDate) && !wellnessDate.isAfter(endDate)) {
                updateWellness(athleteId, wellness)
            }
        }

/*
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
        */
    }

    /**
     * Sends a filled DTO to update wellness metrics for a specific date.
     */
    private fun updateWellness(athleteId: String, wellness: Wellness) {
        val requestDTO = IcuWellnessConverter(wellness).toDTO()
        icuWellnessApiClient.updateWellness(athleteId, requestDTO)
    }

    /**
     * Sends an empty DTO to reset/clear all wellness metrics for a specific date.
     */
    private fun clearWellness(athleteId: String, date: String) {
        val emptyWellness = IcuWellness(id = date)
        icuWellnessApiClient.updateWellness(athleteId, emptyWellness)
    }

}
