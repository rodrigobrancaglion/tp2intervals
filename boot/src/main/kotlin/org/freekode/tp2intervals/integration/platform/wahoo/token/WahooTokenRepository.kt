package org.freekode.tp2intervals.integration.platform.wahoo.token

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.wahoo.configuration.WahooConfigurationRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@CacheConfig(cacheNames = ["wahooAccessTokenCache"])
@Repository
class WahooTokenRepository(
    private val wahooTokenApiClient: WahooTokenApiClient,
    private val wahooConfigurationRepository: WahooConfigurationRepository,
) {
    @Cacheable(key = "'singleton'")
    fun getAccessToken(): String {
        val config = wahooConfigurationRepository.getConfiguration()
        if (!config.isValid()) {
            throw PlatformException(Platform.WAHOO, "Wahoo is not configured")
        }
        val tokenDTO = wahooTokenApiClient.refreshToken(
            mapOf(
                "client_id" to config.clientId!!,
                "client_secret" to config.clientSecret!!,
                "refresh_token" to config.refreshToken!!,
                "grant_type" to "refresh_token",
            )
        )
        
        // Save the rotated refresh token back to the database
        wahooConfigurationRepository.saveRefreshToken(tokenDTO.refresh_token)
        
        return tokenDTO.access_token
    }
}
