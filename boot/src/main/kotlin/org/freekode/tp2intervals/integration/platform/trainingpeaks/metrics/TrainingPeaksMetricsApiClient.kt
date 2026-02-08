package org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TrainingPeaksApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "TrainingPeaksMetricsApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TrainingPeaksApiClientConfig::class]
)
interface TrainingPeaksMetricsApiClient {
    @GetMapping("/metrics/v3/athletes/{athleteId}/consolidatedtimedmetrics/{startDate}/{endDate}")
    fun getMetrics(
        @PathVariable("athleteId") userId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String
    ): List<TPMetricsDTO>

    @PutMapping("/metrics/v3/athletes/{athleteId}/consolidatedtimedmetric")
    fun createMetrics(
        @PathVariable athleteId: String,
        @RequestBody requestDTO: TPMetricsDTO
    )

    @DeleteMapping("/metrics/v3/athletes/{athleteId}/consolidatedtimedmetric")
    fun deleteMetrics(
        @PathVariable athleteId: String,
        @RequestBody requestDTO: TPMetricsDTO
    )
}
