package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SettingService(
    repositories: List<ISettingsRepository>,
    private val configurationRepository: IConfigurationRepository
) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val repositoryMap = repositories.associateBy { it.platform() }

    fun syncPowerZones(
        sourcePlatform: Platform = Platform.TRAINING_PEAKS,
        targetPlatform1: Platform = Platform.INTERVALS,
//        targetPlatform2: Platform = Platform.STRAVA,
        targetPlatform3: Platform = Platform.WAHOO
    ): Boolean {
        return try {
            val sourceRepo  = getRepository(sourcePlatform)
            val targetRepo1 = getRepository(targetPlatform1)
//            val targetRepo2 = getRepository(targetPlatform2)
            val targetRepo3 = getRepository(targetPlatform3)

            val (threshold, zones) = sourceRepo.getPowerZones()
            targetRepo1.savePowerZones(threshold, zones)
//            targetRepo2.savePowerZones(threshold, zones)
            targetRepo3.savePowerZones(threshold, zones)

            log.info("Successfully synced Power Zones from $sourcePlatform to all targets via Repositories")
            true
        } catch (e: Exception) {
            log.error("Error syncing power zones", e)
            false
        }
    }

    private fun getRepository(platform: Platform) = repositoryMap[platform]!!

    fun isSchedulerEnabled(): Boolean {
        return configurationRepository.getConfiguration("setting_power_scheduler_enabled")?.toBoolean() ?: false
    }

    fun setSchedulerEnabled(enabled: Boolean) {
        log.info("Setting scheduler enabled flag to: \$enabled")
        configurationRepository.updateConfig(
            UpdateConfigurationRequest(mapOf("setting_power_scheduler_enabled" to enabled.toString()))
        )
    }
}
