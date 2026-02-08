package org.freekode.tp2intervals.integration.platform.intervalsicu

import feign.RequestInterceptor
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.utils.Auth
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class IntervalsApiClientConfig(
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val apiKey = intervalsConfigurationRepository.getConfiguration().apiKey
            val authorization = Auth.getAuthorizationHeader(apiKey)
            template.header(HttpHeaders.AUTHORIZATION, authorization)
        }
    }

}
