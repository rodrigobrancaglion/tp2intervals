package org.freekode.tp2intervals.controller.events

import org.freekode.tp2intervals.domain.OtherType
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.service.EventService
import org.freekode.tp2intervals.service.ScheduledJobService
import org.springframework.web.bind.annotation.*

@RestController
class EventsController(
    private val eventService: EventService,
    private val scheduledJobService: ScheduledJobService,
) {

    @PostMapping("/api/events/copy-calendar-to-calendar")
    fun copyEvents(@RequestBody request: CopyC2CRequest) =
        eventService.syncEvents(request)

    @PostMapping("/api/events/copy-calendar-to-calendar/schedule")
    fun scheduleEvents(
        @RequestParam platform: String,
        @RequestBody request: C2CTodayScheduledRequest
    ) = scheduledJobService.addRequest(request, platform)

    @GetMapping("/api/events/copy-calendar-to-calendar/schedule")
    fun getScheduleRequests(@RequestParam platform: String) =
        scheduledJobService.getRequests<OtherType>(platform)

    @DeleteMapping("/api/events/copy-calendar-to-calendar/schedule/{id}")
    fun deleteScheduleRequest(@PathVariable id: Int) =
        scheduledJobService.deleteRequest(id)
}
