package org.freekode.tp2intervals.controller.settings

import org.freekode.tp2intervals.service.SettingService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/settings")
class SettingController(
    private val settingService: SettingService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/power/sync")
    fun syncPowerZones(): ResponseEntity<Map<String, String>> {
        log.info("Manual trigger: Syncing power zones from TrainingPeaks to Intervals.icu")

        val success = settingService.syncPowerZones()

        val response = mutableMapOf(
            "status" to if (success) "success" else "error",
            "message" to if (success) "Power zones synced successfully" else "Failed to sync power zones"
        )

        return if (success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.internalServerError().body(response)
        }
    }

}
