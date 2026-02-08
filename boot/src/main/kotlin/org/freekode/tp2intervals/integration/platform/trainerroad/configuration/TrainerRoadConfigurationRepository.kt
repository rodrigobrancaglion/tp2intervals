package org.freekode.tp2intervals.integration.platform.trainerroad.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.CatchFeignException
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["platformInfoCache"])
class TrainerRoadConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val trainerRoadValidationApiClient: TrainerRoadValidationApiClient,
    private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository, PlatformInfoRepository {
    override fun platform() = Platform.TRAINER_ROAD

    @CatchFeignException(platform = Platform.TRAINER_ROAD)
    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")!!.evict(platform().key)
        val updatedConfig = request.getByPrefix(platform().key)
        if (updatedConfig.isEmpty()) {
            return
        }
        val currentConfig =
            iConfigurationRepository.getConfigurationByPrefix(platform().key)
        val newConfig = currentConfig.configMap + updatedConfig
        validateConfiguration(newConfig, true)
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(newConfig))
    }

    @Cacheable(key = "'trainer-road'")
    override fun platformInfo(): PlatformInfo {
        val infoMap = mapOf(
            "isValid" to isValid(),
        )
        return PlatformInfo(infoMap)
    }

    fun getConfiguration(): TrainerRoadConfiguration {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return TrainerRoadConfiguration(config)
    }

    private fun isValid(): Boolean {
        try {
            val currentConfig =
                iConfigurationRepository.getConfigurationByPrefix(platform().key)
            validateConfiguration(currentConfig.configMap, false)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun validateConfiguration(newConfig: Map<String, String?>, ignoreEmpty: Boolean) {
        val config = TrainerRoadConfiguration(newConfig)
        if (!config.canValidate() && ignoreEmpty) {
            return
        }
        val member = trainerRoadValidationApiClient.getMember(config.authCookie ?: "")
        if (member == null || member.MemberId == -1L) {
            throw PlatformException(Platform.TRAINER_ROAD, "Access Denied")
        }
    }
}
