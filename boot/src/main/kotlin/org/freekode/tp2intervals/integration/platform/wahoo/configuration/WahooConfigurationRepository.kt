package org.freekode.tp2intervals.integration.platform.wahoo.configuration

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
class WahooConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository, PlatformInfoRepository {
    override fun platform() = Platform.WAHOO

    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")!!.evict(platform().key)
        val updatedConfig = request.getByPrefix(platform().key)
        if (updatedConfig.isEmpty()) {
            return
        }
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(updatedConfig))
    }

    fun saveRefreshToken(refreshToken: String) {
        cacheManager.getCache("platformInfoCache")!!.evict(platform().key)
        iConfigurationRepository.updateConfig(
            UpdateConfigurationRequest(
                mapOf("${platform().key}.refresh-token" to refreshToken)
            )
        )
    }

    fun getConfiguration(): WahooConfiguration {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return WahooConfiguration(config)
    }

    @Cacheable(key = "'wahoo'")
    override fun platformInfo(): PlatformInfo {
        val infoMap = mapOf(
            "isValid" to getConfiguration().isValid(),
        )
        return PlatformInfo(infoMap)
    }
}
