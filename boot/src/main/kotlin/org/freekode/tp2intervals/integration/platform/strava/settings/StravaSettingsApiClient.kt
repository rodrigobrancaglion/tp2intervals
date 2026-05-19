package org.freekode.tp2intervals.integration.platform.strava.settings

import org.freekode.tp2intervals.integration.platform.strava.activity.StravaActivityClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "stravaSettingsApiClient",
    url = "\${app.strava.api-url}/api/v3",
    configuration = [StravaActivityClientConfig::class]
)
interface StravaSettingsApiClient {

    @PutMapping("/athlete")
    fun updateAthlete(@RequestBody payload: Map<String, Any?>)
}
