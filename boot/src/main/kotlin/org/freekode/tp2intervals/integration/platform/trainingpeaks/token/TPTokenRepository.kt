package org.freekode.tp2intervals.integration.platform.trainingpeaks.token

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.trainingpeaks.configuration.TPConfigurationRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@CacheConfig(cacheNames = ["tpAccessTokenCache"])
@Component
class TPTokenRepository(
    private val TPTokenApiClient: TPTokenApiClient,
    private val TPConfigurationRepository: TPConfigurationRepository,
) {
    @Cacheable(key = "'singleton'")
    fun getToken(): String {
        val authCookie = TPConfigurationRepository.getConfiguration().authCookie
            ?: throw PlatformException(Platform.TRAINING_PEAKS, "Access to the platform is not configured")
        val token = TPTokenApiClient.getToken(authCookie)
        return token.accessToken!!
    }

}
