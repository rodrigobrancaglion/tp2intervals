package org.freekode.tp2intervals.integration.platform.intervalsicu

import feign.RequestInterceptor
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.utils.Auth
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class IcuApiClientConfig(
    private val icuConfigurationRepository: IcuConfigurationRepository
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val apiKey = icuConfigurationRepository.getConfiguration().apiKey
            val authorization = Auth.getAuthorizationHeader(apiKey)
            template.header(HttpHeaders.AUTHORIZATION, authorization)
        }
    }

}
