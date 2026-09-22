package org.freekode.tp2intervals.integration.platform.rouvy.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.rouvy.configuration.dto.RouvyConfigurationDTO
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["platformInfoCache"])
class RouvyConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val cacheManager: CacheManager
) : IPlatformConfigurationRepository, PlatformInfoRepository {
    override fun platform() = Platform.ROUVY

    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")?.evict(org.freekode.tp2intervals.utils.UserContextHolder.username + "-" + platform().key)
        val updatedConfig = request.getByPrefix(platform().key)
        if (updatedConfig.isEmpty()) {
            return
        }
        val currentConfig = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        val newConfig = currentConfig.configMap + updatedConfig
        validateConfiguration(newConfig, true)
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(newConfig))
    }

    @Cacheable(keyGenerator = "userKeyGenerator")
    override fun platformInfo(): PlatformInfo {
        val infoMap = mapOf(
            "isValid" to isValid(),
        )
        return PlatformInfo(infoMap)
    }

    fun getConfiguration(): RouvyConfigurationDTO {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return RouvyConfigurationDTO(config)
    }

    private fun isValid(): Boolean {
        return try {
            val currentConfig = iConfigurationRepository.getConfigurationByPrefix(platform().key)
            validateConfiguration(currentConfig.configMap, false)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun validateConfiguration(newConfig: Map<String, String?>, ignoreEmpty: Boolean) {
        val email = newConfig["${Platform.ROUVY.key}.email"]
        val password = newConfig["${Platform.ROUVY.key}.password"]
        if (email.isNullOrBlank() && password.isNullOrBlank()) {
            if (ignoreEmpty) {
                return
            }
            throw PlatformException(platform(), "Access to the platform is not configured")
        }
        try {
            RouvyConfigurationDTO(newConfig)
        } catch (e: NullPointerException) {
            throw PlatformException(platform(), "Access to the platform is not configured")
        }
    }
}
