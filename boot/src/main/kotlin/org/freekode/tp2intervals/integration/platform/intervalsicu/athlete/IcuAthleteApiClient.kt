package org.freekode.tp2intervals.integration.platform.intervalsicu.athlete

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    value = "IntervalsAthleteApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
)
interface IcuAthleteApiClient {
    @GetMapping("/api/v1/athlete/{athleteId}")
    fun getAthlete(
        @PathVariable("athleteId") athleteId: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String
    ): Map<String, Any>
}