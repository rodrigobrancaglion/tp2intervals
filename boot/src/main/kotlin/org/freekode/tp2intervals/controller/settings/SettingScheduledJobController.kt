package org.freekode.tp2intervals.controller.settings

import org.freekode.tp2intervals.service.SettingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/settings")
class SettingScheduledJobController(
    private val settingService: SettingService
) {
    @GetMapping("/power/scheduler")
    fun getSchedulerStatus(): ResponseEntity<Map<String, Boolean>> {
        return ResponseEntity.ok(mapOf("enabled" to settingService.isSchedulerEnabled()))
    }

    @PostMapping("/power/scheduler")
    fun toggleScheduler(@RequestBody body: Map<String, Boolean>): ResponseEntity<Map<String, Boolean>> {
        val enabled = body["enabled"] ?: false
        settingService.setSchedulerEnabled(enabled)
        return ResponseEntity.ok(mapOf("enabled" to settingService.isSchedulerEnabled()))
    }
}
