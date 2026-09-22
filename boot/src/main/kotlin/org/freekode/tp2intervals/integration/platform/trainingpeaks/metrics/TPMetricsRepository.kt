package org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics.dto.TPMetricsDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TPUserRepository
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate


@CacheConfig(cacheNames = ["tpMetricsCache"])
@Repository
class TPMetricsRepository(
    private val TPMetricsApiClient: TPMetricsApiClient,
    private val TPUserRepository: TPUserRepository,
) : IWellnessRepository {

    override fun platform() = Platform.TRAINING_PEAKS

    override fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness> {
        val athleteId = TPUserRepository.getUser().userId
        val listMetricsDTO = TPMetricsApiClient.getMetrics(
            athleteId,
            startDate.toString(),
            endDate.toString())

        val listWellness = listMetricsDTO.map {
            TPMetricsConverter(it).toDomain()
        }

        return listWellness
    }


    override fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate) {
        val athleteId = TPUserRepository.getUser().userId

        wellnesses.filterNotNull().forEach { wellness ->
            val requestDTO = TPMetricsConverter(wellness).toDTO(athleteId)
            if (requestDTO.getMetricWeight() == -1.0) {
                deleteWellness(athleteId, requestDTO)
            } else {
                TPMetricsApiClient.createMetrics(athleteId, requestDTO)
            }
        }
    }

    private fun deleteWellness(athleteId: String, requestDTO: TPMetricsDTO) {
        val existingMetrics = TPMetricsApiClient.getMetrics(
            athleteId,
            requestDTO.timeStamp.toString().take(10),
            requestDTO.timeStamp.toString().take(10)
        )

        existingMetrics.forEach { metric ->
            metric.getDetailByType(WellnessType.WEIGHT)?.let { weightDetail ->
                weightDetail.value = -1.0
                TPMetricsApiClient.deleteMetrics(athleteId, metric)
            }
        }
    }

}
