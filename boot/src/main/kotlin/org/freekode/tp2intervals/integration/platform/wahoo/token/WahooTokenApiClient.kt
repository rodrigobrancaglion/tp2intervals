package org.freekode.tp2intervals.integration.platform.wahoo.token

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "wahooTokenApiClient", url = "\${app.wahoo.api-url}")
interface WahooTokenApiClient {
    @PostMapping(value = ["/oauth/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun refreshToken(@RequestParam params: Map<String, String>): WahooTokenDTO

    @PostMapping(value = ["/oauth/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun exchangeCode(@RequestParam params: Map<String, String>): WahooTokenDTO
}
