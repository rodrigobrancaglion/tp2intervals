package org.freekode.tp2intervals.integration.platform.trainingpeaks.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.CatchFeignException
import org.freekode.tp2intervals.integration.platform.trainingpeaks.token.TrainingPeaksTokenApiClient
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

@Service
class TrainingPeaksConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val trainingPeaksTokenApiClient: TrainingPeaksTokenApiClient,
    private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    @CatchFeignException(platform = Platform.TRAINING_PEAKS)
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

    fun getConfiguration(): TrainingPeaksConfiguration {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return TrainingPeaksConfiguration(config)
    }

    fun isValid(): Boolean {
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
        val tpConfig = TrainingPeaksConfiguration(newConfig)
        if (!tpConfig.canValidate() && ignoreEmpty) {
            return
        }
        trainingPeaksTokenApiClient.getToken(tpConfig.authCookie ?: "")
    }
}
