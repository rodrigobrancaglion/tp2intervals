package org.freekode.tp2intervals.config

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.springframework.stereotype.Component


@Component
class DefaultConfigurationInitializer(
    private val defaultConfiguration: DefaultConfiguration,
    private val iConfigurationRepository: IConfigurationRepository
) {
     private val logger = AppLogger.get(this.javaClass)

    init {
        initDefaultProperties()
    }

    private fun initDefaultProperties() {
        if (defaultConfiguration.defaultConfig == null) {
            logger.infoL3In("Default configuration is empty")
            return
        }
        logger.infoL3In("Initializing default configuration")
        val request = UpdateConfigurationRequest(defaultConfiguration.defaultConfig)
        iConfigurationRepository.updateConfig(request)
    }
}
