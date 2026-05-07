package org.freekode.tp2intervals.integration.platform.trainingpeaks.event

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TrainingPeaksApiClientConfig
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "TrainingPeaksEventApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TrainingPeaksApiClientConfig::class]
)
interface TrainingPeaksEventApiClient {

    /**
     * Returns all events (races) for the athlete in the given date range.
     * URL: GET /fitness/v6/athletes/{athleteId}/events/{startDate}/{endDate}
     */
    @GetMapping("/fitness/v6/athletes/{athleteId}/events/{startDate}/{endDate}")
    fun getEvents(
        @PathVariable("athleteId") athleteId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String,
    ): List<TPEventDTO>

    @PostMapping("/fitness/v6/athletes/{athleteId}/event")
    fun createEvent(
        @PathVariable("athleteId") athleteId: String,
        @RequestBody event: TPEventDTO,
    ): TPEventDTO
}
