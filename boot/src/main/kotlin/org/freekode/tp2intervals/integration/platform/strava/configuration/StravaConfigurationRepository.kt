package org.freekode.tp2intervals.integration.platform.strava.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["platformInfoCache"])
class StravaConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository, PlatformInfoRepository {
    override fun platform() = Platform.STRAVA

    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")?.evict(org.freekode.tp2intervals.utils.UserContextHolder.username + "-" + platform().key)
        val updatedConfig = request.getByPrefix(platform().key)
        if (updatedConfig.isEmpty()) {
            return
        }
        val currentConfig = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        val newConfig = currentConfig.configMap + updatedConfig
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(newConfig))
    }

    /**
     * Returns the current Strava configuration reading credentials from the DB.
     */
    fun getConfiguration(): StravaConfiguration {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return StravaConfiguration(config)
    }

    @Cacheable(keyGenerator = "userKeyGenerator")
    override fun platformInfo(): PlatformInfo {
        val infoMap = mapOf(
            "isValid" to getConfiguration().isValid(),
        )
        return PlatformInfo(infoMap)
    }
}
