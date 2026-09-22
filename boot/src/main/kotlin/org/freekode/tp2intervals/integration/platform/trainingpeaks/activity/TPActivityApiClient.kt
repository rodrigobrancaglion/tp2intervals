package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "TrainingPeaksActivityApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TPApiClientConfig::class]
)
interface TPActivityApiClient {
    @PutMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}", consumes = ["application/json"])
    fun updateActivity(
        @PathVariable userId: String,
        @PathVariable workoutId: Long,
        @RequestBody requestDTO: String
    )

    @PostMapping(value = ["/fitness/v6/athletes/{userId}/workouts/filedata"])
    fun uploadActivity(
        @RequestHeader("userId") userId: String,
        @RequestBody request: TPUploadRequest
    ): ResponseEntity<String>
}