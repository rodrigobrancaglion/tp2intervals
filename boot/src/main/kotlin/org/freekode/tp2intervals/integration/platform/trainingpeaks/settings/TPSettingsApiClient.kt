package org.freekode.tp2intervals.integration.platform.trainingpeaks.settings

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPApiClientConfig
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.TPSettingsResponseDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    value = "TrainingPeaksSettingsApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TPApiClientConfig::class]
)
interface TPSettingsApiClient {
    @GetMapping("/fitness/v1/athletes/{userId}/settings")
    fun getSettings(
        @PathVariable userId: String,
    ): TPSettingsResponseDTO
}