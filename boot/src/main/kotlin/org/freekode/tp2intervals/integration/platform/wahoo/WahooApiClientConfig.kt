package org.freekode.tp2intervals.integration.platform.wahoo

import feign.RequestInterceptor
import org.freekode.tp2intervals.integration.platform.wahoo.token.WahooTokenRepository
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class WahooApiClientConfig(
    private val wahooTokenRepository: WahooTokenRepository,
) {
    @Bean
    fun wahooRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val token = wahooTokenRepository.getAccessToken()
            template.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }
    }
}