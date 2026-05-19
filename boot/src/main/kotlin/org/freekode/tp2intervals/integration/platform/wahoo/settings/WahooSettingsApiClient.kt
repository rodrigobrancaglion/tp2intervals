package org.freekode.tp2intervals.integration.platform.wahoo.settings

import org.freekode.tp2intervals.integration.platform.wahoo.WahooApiClientConfig
import org.freekode.tp2intervals.integration.platform.wahoo.settings.dto.WahooPowerZoneDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "wahooSettingsApiClient",
    url = "\${app.wahoo.api-url}",
    configuration = [WahooApiClientConfig::class]
)
interface WahooSettingsApiClient {
    @GetMapping("/v1/power_zones")
    fun getPowerZones(): List<WahooPowerZoneDTO>

    @PostMapping(value = ["/v1/power_zones"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun createPowerZone(@RequestBody params: Map<String, Any>): WahooPowerZoneDTO

    @PutMapping(value = ["/v1/power_zones/{id}"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun updatePowerZone(@PathVariable("id") id: Long, @RequestBody params: Map<String, Any>): WahooPowerZoneDTO
}
