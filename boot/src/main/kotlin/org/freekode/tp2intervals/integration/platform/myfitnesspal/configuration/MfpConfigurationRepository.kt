package org.freekode.tp2intervals.integration.platform.myfitnesspal.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.CatchFeignException
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["platformInfoCache"])
class MfpConfigurationRepository(
        private val iConfigurationRepository: IConfigurationRepository,
        private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository, PlatformInfoRepository {

    override fun platform() = Platform.MYFITNESSPAL

    @CatchFeignException(platform = Platform.MYFITNESSPAL)
    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")!!.evict(platform().key)
        val updatedConfig = request.getByPrefix(platform().key)
        if (updatedConfig.isEmpty()) {
            return
        }
        val currentConfig = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        val newConfig = currentConfig.configMap + updatedConfig
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(newConfig))
    }

    @Cacheable(key = "'mfp'")
    override fun platformInfo(): PlatformInfo {
        val infoMap =
                mapOf(
                        "isValid" to isValid(),
                )
        return PlatformInfo(infoMap)
    }

    fun getConfiguration(): MfpConfiguration {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return MfpConfiguration(config)
    }

    private fun isValid(): Boolean {
        return try {
            getConfiguration().isValid()
        } catch (e: Exception) {
            false
        }
    }
}
