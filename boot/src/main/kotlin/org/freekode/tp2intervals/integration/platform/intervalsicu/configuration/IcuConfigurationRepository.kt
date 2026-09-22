package org.freekode.tp2intervals.integration.platform.intervalsicu.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.CatchFeignException
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.IcuAthleteApiClient
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.dto.IcuConfigurationDTO
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.freekode.tp2intervals.integration.utils.Auth
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
@CacheConfig(cacheNames = ["platformInfoCache"])
class IcuConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    private val icuAthleteApiClient: IcuAthleteApiClient,
    private val cacheManager: CacheManager,
) : IPlatformConfigurationRepository, PlatformInfoRepository {
    override fun platform() = Platform.INTERVALS

    @CatchFeignException(platform = Platform.INTERVALS)
    override fun updateConfig(request: UpdateConfigurationRequest) {
        cacheManager.getCache("platformInfoCache")?.evict(org.freekode.tp2intervals.utils.UserContextHolder.username + "-" + platform().key)
        val newConfig = getConfigToUpdate(request)
        validateConfiguration(newConfig)
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(newConfig))
    }

    @Cacheable(keyGenerator = "userKeyGenerator")
    override fun platformInfo(): PlatformInfo {
        val infoMap = mapOf(
            "isValid" to isValid(),
        )
        return PlatformInfo(infoMap)
    }

    fun getConfiguration(): IcuConfigurationDTO {
        val config = iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return IcuConfigurationDTO(config)
    }

    private fun isValid(): Boolean {
        try {
            val currentConfig =
                iConfigurationRepository.getConfigurationByPrefix(platform().key)
            validateConfiguration(currentConfig.configMap)
            return true
        } catch (e: PlatformException) {
            return false
        }
    }

    private fun getConfigToUpdate(request: UpdateConfigurationRequest): Map<String, String?> {
        val currentConfig =
            iConfigurationRepository.getConfigurationByPrefix(platform().key)
        return currentConfig.configMap + request.getByPrefix(platform().key)
    }

    private fun validateConfiguration(newConfig: Map<String, String?>) {
        val intervalsConfig: IcuConfigurationDTO
        try {
            intervalsConfig = IcuConfigurationDTO(newConfig)
        } catch (e: NullPointerException) {
            throw PlatformException(platform(), "Access to the platform is not configured")
        }

        icuAthleteApiClient.getAthlete(
            intervalsConfig.athleteId,
            Auth.getAuthorizationHeader(intervalsConfig.apiKey)
        )
    }
}
