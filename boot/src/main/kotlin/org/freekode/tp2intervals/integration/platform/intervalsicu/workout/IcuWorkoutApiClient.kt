package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.integration.platform.intervalsicu.IcuApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IcuWorkoutEx
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "IntervalsWorkoutApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IcuApiClientConfig::class]
)
interface IcuWorkoutApiClient {

    @PostMapping("/api/v1/athlete/{athleteId}/workouts/bulk")
    fun createWorkouts(
        @PathVariable athleteId: String,
        @RequestBody requests: List<IcuWorkoutEx>
    )

    @PostMapping("/api/v1/athlete/{athleteId}/events/bulk")
    fun createEventsBulk(
        @PathVariable athleteId: String,
        @RequestBody requests: List<IcuEventEx>
    )

    @PostMapping("/api/v1/athlete/{athleteId}/events")
    fun createEvent(
        @PathVariable athleteId: String,
        @RequestBody icuEventEx: IcuEventEx
    )

    @GetMapping(
        "/api/v1/athlete/{id}/events" +
                "?oldest={startDate}&" +
                "newest={endDate}&" +
                "resolve=true&" +
                "powerRange={powerRange}&" +
                "hrRange={hrRange}&" +
                "paceRange={paceRange}"
    )
    fun getEvents(
        @PathVariable id: String,
        @PathVariable startDate: String,
        @PathVariable endDate: String,
        @PathVariable powerRange: Float,
        @PathVariable hrRange: Float,
        @PathVariable paceRange: Float,
    ): List<IcuEventEx>

    @GetMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun getEvents(
        @PathVariable eventId: String,
    ): IcuEventEx

    @PutMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun updateEvent(
        @PathVariable athleteId: String,
        @PathVariable eventId: Long,
        @RequestBody requestDTO: IcuEventEx
    )

    @DeleteMapping("/api/v1/athlete/{athleteId}/events/{eventId}")
    fun deleteEvent(
        @PathVariable athleteId: String,
        @PathVariable eventId: Long,
    )

}