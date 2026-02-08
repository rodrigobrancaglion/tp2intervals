package org.freekode.tp2intervals.controller.wellness

import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.wellness.CopyWellnessResponse
import org.freekode.tp2intervals.service.WellnessService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WellnessController(
    private val wellnessService: WellnessService,
) {
    @PostMapping("/api/wellness/copy-calendar-to-calendar")
    fun copyWellnessFromCalendarToCalendar(@RequestBody request: CopyC2CRequest): CopyWellnessResponse {
        return wellnessService.copyWellnessC2C(request)
    }
}
