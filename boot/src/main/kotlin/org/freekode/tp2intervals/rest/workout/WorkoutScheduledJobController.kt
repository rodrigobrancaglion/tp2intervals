package org.freekode.tp2intervals.rest.workout

import org.freekode.tp2intervals.app.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.app.workout.schedule.WorkoutScheduledJob
import org.springframework.web.bind.annotation.*

@RestController
class WorkoutScheduledJobController(
    private val workoutScheduledJob: WorkoutScheduledJob
) {
    @PostMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(@RequestBody request: C2CTodayScheduledRequest) {
        workoutScheduledJob.addRequest(request)
    }

    @GetMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests() =
        workoutScheduledJob.getRequests()

    @DeleteMapping("/api/workout/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        workoutScheduledJob.deleteRequest(id)
}
