package org.freekode.tp2intervals.controller.workout

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.ScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class WorkoutScheduledJobController(
    private val scheduledJob: ScheduledJobService
) {
    @PostMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(
        @RequestParam platform: String,
        @RequestBody request: C2CTodayScheduledRequest
    ) {
        scheduledJob.addRequest(request, platform)
    }

    @GetMapping("/api/workout/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests(@RequestParam platform: String) =
        scheduledJob.getRequests<TrainingType>(platform)

    @DeleteMapping("/api/workout/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        scheduledJob.deleteRequest(id)
}

