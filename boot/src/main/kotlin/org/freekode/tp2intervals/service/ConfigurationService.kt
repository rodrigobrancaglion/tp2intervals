package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.AppConfiguration
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.configuration.IPlatformConfigurationRepository
import org.springframework.stereotype.Service

@Service
class ConfigurationService(
    private val platformConfigurationRepositories: List<IPlatformConfigurationRepository>,
    platformInfoRepositories: List<PlatformInfoRepository>,
    private val iConfigurationRepository: IConfigurationRepository,
    private val debugModeService: DebugModeService,
) {
    private val platformInfoRepositoryMap = platformInfoRepositories.associateBy { it.platform() }

    fun getConfiguration(key: String): String? = iConfigurationRepository.getConfiguration(key)

    fun getConfigurations(): AppConfiguration = iConfigurationRepository.getConfigurations()

    fun updateConfiguration(request: UpdateConfigurationRequest): List<String> {
        val errors = platformConfigurationRepositories.mapNotNull { updateConfiguration(request, it) }
        handleDebugModeIfNecessary(request)
        return errors
    }

    fun platformInfo() =
        platformInfoRepositoryMap.entries.associate { it.key to it.value.platformInfo() }

    fun platformInfo(platform: Platform): PlatformInfo {
        return platformInfoRepositoryMap[platform]!!.platformInfo()
    }

    private fun handleDebugModeIfNecessary(request: UpdateConfigurationRequest) {
        debugModeService.handleDebugMode(request.config)
    }

    private fun updateConfiguration(
        request: UpdateConfigurationRequest,
        repository: IPlatformConfigurationRepository
    ): String? {
        return try {
            repository.updateConfig(request)
            null
        } catch (e: PlatformException) {
            "${e.platform.title}: ${e.message}"
        } catch (e: Exception) {
            e.message
        }
    }
}