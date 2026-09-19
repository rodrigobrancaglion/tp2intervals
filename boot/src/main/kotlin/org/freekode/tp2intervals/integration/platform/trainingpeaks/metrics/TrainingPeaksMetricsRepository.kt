package org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics.dto.TPMetricsDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate


@CacheConfig(cacheNames = ["tpMetricsCache"])
@Repository
class TrainingPeaksMetricsRepository(
    private val trainingPeaksMetricsApiClient: TrainingPeaksMetricsApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
) : IWellnessRepository {

    override fun platform() = Platform.TRAINING_PEAKS

    override fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness> {
        val athleteId = trainingPeaksUserRepository.getUser().userId
        val listMetricsDTO = trainingPeaksMetricsApiClient.getMetrics(
            athleteId,
            startDate.toString(),
            endDate.toString())

        val listWellness = listMetricsDTO.map {
            TPMetricsConverter(it).toDomain()
        }

        return listWellness
    }


    override fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate) {
        val athleteId = trainingPeaksUserRepository.getUser().userId

        wellnesses.filterNotNull().forEach { wellness ->
            val requestDTO = TPMetricsConverter(wellness).toDTO(athleteId)
            if (requestDTO.getMetricWeight() == -1.0) {
                deleteWellness(athleteId, requestDTO)
            } else {
                trainingPeaksMetricsApiClient.createMetrics(athleteId, requestDTO)
            }
        }
    }

    private fun deleteWellness(athleteId: String, requestDTO: TPMetricsDTO) {
        val existingMetrics = trainingPeaksMetricsApiClient.getMetrics(
            athleteId,
            requestDTO.timeStamp.toString().take(10),
            requestDTO.timeStamp.toString().take(10)
        )

        existingMetrics.forEach { metric ->
            metric.getDetailByType(WellnessType.WEIGHT)?.let { weightDetail ->
                weightDetail.value = -1.0
                trainingPeaksMetricsApiClient.deleteMetrics(athleteId, metric)
            }
        }
    }

}
