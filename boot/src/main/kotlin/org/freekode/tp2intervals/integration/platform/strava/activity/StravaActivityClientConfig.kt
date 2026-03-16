package org.freekode.tp2intervals.integration.platform.strava.activity

import feign.RequestInterceptor
import org.freekode.tp2intervals.integration.platform.strava.token.StravaTokenRepository
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

/**
 * Feign configuration that injects the Strava Bearer token into every request.
 * Follows the same pattern as TrainingPeaksApiClientConfig.
 */
class StravaActivityClientConfig(
    private val stravaTokenRepository: StravaTokenRepository,
) {
    @Bean
    fun stravaRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val token = stravaTokenRepository.getAccessToken()
            template.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }
    }
}
