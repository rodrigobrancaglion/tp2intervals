package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.EventRequestDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IntervalsEventCommentDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IntervalsEventDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.WorkoutRequestDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

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
        @RequestBody requests: List<WorkoutRequestDTO>
    )

    @PostMapping("/api/v1/athlete/{athleteId}/events")
    fun createEvent(
        @PathVariable athleteId: String,
        @RequestBody eventRequestDTO: EventRequestDTO
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

    @GetMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun getEvents(
        @PathVariable eventId: String,
    ): IntervalsEventDTO

    @PostMapping("/api/v1/chats/send-message")
    fun createComment(
        @RequestBody requestDTO: IntervalsEventCommentDTO
    )

    @PutMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun updateEvent(
        @PathVariable athleteId: String,
        @PathVariable eventId: Long,
        @RequestBody requestDTO: EventRequestDTO
    )

    @DeleteMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun deleteEvent(
        @PathVariable athleteId: String,
        @PathVariable eventId: Long,
    )
}