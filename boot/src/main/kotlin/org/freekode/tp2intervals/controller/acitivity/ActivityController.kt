package org.freekode.tp2intervals.controller.acitivity

import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.service.ActivityService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ActivityController(
    private val activityService: ActivityService,
) {

    @PostMapping("/api/activities/copy")
    fun syncActivities(@RequestBody request: CopyC2CRequest) =
        activityService.syncActivities(request)

    @PostMapping("/api/activities/copy-calendar-to-calendar")
    fun copyActivities(@RequestBody request: CopyC2CRequest) =
        activityService.syncActivities(request)
}
