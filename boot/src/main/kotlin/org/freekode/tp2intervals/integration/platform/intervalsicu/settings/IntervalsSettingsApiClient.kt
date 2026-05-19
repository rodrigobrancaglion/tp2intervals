package org.freekode.tp2intervals.integration.platform.intervalsicu.settings

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.settings.dto.IntervalsSportSettingsDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "IntervalsSettingsApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IntervalsApiClientConfig::class]
)
interface IntervalsSettingsApiClient {
    @GetMapping("/api/v1/athlete/{athleteId}/sport-settings/{sportSettingsId}")
    fun getSportSettings(
        @PathVariable athleteId: String,
        @PathVariable sportSettingsId: Int?,
    ): IntervalsSportSettingsDTO

    @PutMapping("/api/v1/athlete/{athleteId}/sport-settings/{sportSettingsId}?recalcHrZones=true")
    fun updateSportSettings(
        @PathVariable athleteId: String,
        @PathVariable sportSettingsId: Int?,
        @RequestBody payload: Map<String, Any>
    )
}
