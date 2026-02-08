package org.freekode.tp2intervals.config

import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class DefaultConfigurationInitializer(
    private val defaultConfiguration: DefaultConfiguration,
    private val iConfigurationRepository: IConfigurationRepository
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        initDefaultProperties()
    }

    private fun initDefaultProperties() {
        if (defaultConfiguration.defaultConfig == null) {
            log.info("Default configuration is empty")
            return
        }
        log.info("Initializing default configuration")
        val request = UpdateConfigurationRequest(defaultConfiguration.defaultConfig)
        iConfigurationRepository.updateConfig(request)
    }
}
