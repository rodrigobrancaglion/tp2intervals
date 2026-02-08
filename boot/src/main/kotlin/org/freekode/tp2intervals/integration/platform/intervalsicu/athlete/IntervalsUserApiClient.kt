package org.freekode.tp2intervals.integration.platform.intervalsicu.athlete

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    value = "IntervalsUserApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IntervalsApiClientConfig::class]
)
interface IntervalsUserApiClient {
    @GetMapping("/api/v1/athlete/{athleteId}")
    fun getUser(
        @PathVariable athleteId: String
    ): IntervalsAthleteProfileDTO
}