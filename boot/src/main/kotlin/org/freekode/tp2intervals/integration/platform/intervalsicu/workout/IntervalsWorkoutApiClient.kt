package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "IntervalsWorkoutApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IntervalsApiClientConfig::class]
)
interface IntervalsWorkoutApiClient {

    @PostMapping("/api/v1/athlete/{athleteId}/workouts/bulk")
    fun createWorkouts(
        @PathVariable athleteId: String,
        @RequestBody requests: List<CreateWorkoutRequestDTO>
    )

    @PostMapping("/api/v1/athlete/{athleteId}/events")
    fun createEvent(
        @PathVariable athleteId: String,
        @RequestBody createEventRequestDTO: CreateEventRequestDTO
    )

    @GetMapping(
        "/api/v1/athlete/{athleteId}/events?" +
                "oldest={startDate}&" +
                "newest={endDate}&" +
                "resolve=true&" +
                "powerRange={powerRange}&" +
                "hrRange={hrRange}&" +
                "paceRange={paceRange}"
    )
    fun getEvents(
        @PathVariable athleteId: String,
        @PathVariable startDate: String,
        @PathVariable endDate: String,
        @PathVariable powerRange: Float,
        @PathVariable hrRange: Float,
        @PathVariable paceRange: Float,
    ): List<IntervalsEventDTO>

    @PostMapping("/api/v1/chats/send-message")
    fun createComment(
        @RequestBody requestDTO: IntervalsEventCommentDTO
    )
}