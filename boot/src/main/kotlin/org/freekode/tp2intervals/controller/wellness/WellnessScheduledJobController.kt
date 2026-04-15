package org.freekode.tp2intervals.controller.wellness

import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.ScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class WellnessScheduledJobController(
    private val scheduledJob: ScheduledJobService
) {
    @PostMapping("/api/wellness/copy-calendar-to-calendar/schedule")
    fun scheduleC2CTodayRequest(
        @RequestParam platform: String,
        @RequestBody request: C2CTodayScheduledRequest
    ) {
        scheduledJob.addRequest(request, platform)
    }

    @GetMapping("/api/wellness/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests(@RequestParam platform: String) =
        scheduledJob.getRequests<WellnessType>(platform)

    @DeleteMapping("/api/wellness/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        scheduledJob.deleteRequest(id)
}

