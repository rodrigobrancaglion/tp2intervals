package org.freekode.tp2intervals.controller.acitivity

import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.ScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class ActivitiesScheduledJobController(
    private val scheduledJob: ScheduledJobService
) {
    @PostMapping("/api/activities/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(@RequestBody request: C2CTodayScheduledRequest) {
        scheduledJob.addRequest(request)
    }

    @GetMapping("/api/activities/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests() =
        scheduledJob.getRequests<ActivityType>()

    @DeleteMapping("/api/activities/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        scheduledJob.deleteRequest(id)

}
