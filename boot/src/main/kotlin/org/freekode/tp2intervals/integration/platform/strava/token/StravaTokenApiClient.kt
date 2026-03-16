package org.freekode.tp2intervals.integration.platform.strava.token

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * Feign client for exchanging a Strava refresh_token for an access_token.
 * The Strava token endpoint requires application/x-www-form-urlencoded in the request body.
 * Using @RequestBody with a Map forces Feign to encode params in the body, not the URL.
 */
@FeignClient(
    value = "StravaTokenApiClient",
    url = "\${app.strava.api-url}",
    dismiss404 = true,
    primary = false,
)
interface StravaTokenApiClient {

    @PostMapping("/oauth/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun refreshToken(@RequestBody params: Map<String, String>): StravaTokenDTO
}
