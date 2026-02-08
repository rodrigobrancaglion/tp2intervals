package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TrainingPeaksApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "TrainingPeaksActivityApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TrainingPeaksApiClientConfig::class]
)
interface TrainingPeaksActivityApiClient {
    @PutMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}", consumes = ["application/json"])
    fun updateActivity(
        @PathVariable userId: String,
        @PathVariable workoutId: Long,
        @RequestBody requestDTO: String
    )
}