package org.freekode.tp2intervals.controller.acitivity

import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.ScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class ActivityScheduledJobController(
    private val scheduledJob: ScheduledJobService
) {
    @PostMapping("/api/activity/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(
        @RequestParam platform: String,
        @RequestBody request: C2CTodayScheduledRequest
    ) {
        scheduledJob.addRequest(request, platform)
    }

    @GetMapping("/api/activity/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests(@RequestParam platform: String) =
        scheduledJob.getRequests<ActivityType>(platform)

    @DeleteMapping("/api/activity/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        scheduledJob.deleteRequest(id)
}

