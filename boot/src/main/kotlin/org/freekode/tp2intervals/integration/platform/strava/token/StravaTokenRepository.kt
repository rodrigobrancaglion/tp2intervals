package org.freekode.tp2intervals.integration.platform.strava.token

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.strava.configuration.StravaConfigurationRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

/**
 * Exchanges the stored Strava refresh_token for a short-lived access_token.
 * The result is cached so we only call Strava once per token lifetime.
 * Parameters are sent as form-encoded body (required by Strava OAuth2 endpoint).
 */
@CacheConfig(cacheNames = ["stravaAccessTokenCache"])
@Component
class StravaTokenRepository(
    private val stravaTokenApiClient: StravaTokenApiClient,
    private val stravaConfigurationRepository: StravaConfigurationRepository,
) {
    @Cacheable(key = "'singleton'")
    fun getAccessToken(): String {
        val config = stravaConfigurationRepository.getConfiguration()
        if (!config.isValid()) {
            throw PlatformException(Platform.STRAVA, "Strava is not configured")
        }
        val tokenDTO = stravaTokenApiClient.refreshToken(
            mapOf(
                "client_id" to config.clientId!!,
                "client_secret" to config.clientSecret!!,
                "refresh_token" to config.refreshToken!!,
                "grant_type" to "refresh_token",
            )
        )
        return tokenDTO.access_token
    }
}
