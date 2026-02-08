package org.freekode.tp2intervals.controller.workout

import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.WorkoutScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class WorkoutScheduledJobController(
    private val workoutScheduledJobService: WorkoutScheduledJobService
) {
    @PostMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(@RequestBody request: C2CTodayScheduledRequest) {
        workoutScheduledJobService.addRequest(request)
    }

    @GetMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests() =
        workoutScheduledJobService.getRequests()

    @DeleteMapping("/api/workout/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        workoutScheduledJobService.deleteRequest(id)
}
