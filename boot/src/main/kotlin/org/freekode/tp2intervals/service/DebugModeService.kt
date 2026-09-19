package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.integration.provider.configuration.IConfigurationRepository
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem
import org.springframework.stereotype.Component

@Component
class DebugModeService(
    private val IConfigurationRepository: IConfigurationRepository
) {
     private val logger = AppLogger.get(this.javaClass)
    private val generalDebugModeKey = "general.debug-mode"

    init {
        initDebugMode()
    }

    fun handleDebugMode(configMap: Map<String, String?>) {
        if (configMap.contains(generalDebugModeKey) && configMap[generalDebugModeKey]!!.toBoolean()) {
            setLogLevel(LogLevel.DEBUG)
            return
        }
        setLogLevel(LogLevel.INFO)
    }

    private fun initDebugMode() {
        val configurations = IConfigurationRepository.getConfigurations().configMap
        handleDebugMode(configurations)
    }

    private fun setLogLevel(logLevel: LogLevel) {
        val system: LoggingSystem = LoggingSystem.get(this::class.java.getClassLoader())
        system.setLogLevel("org.freekode.tp2intervals", logLevel)
        logger.errorL3In("Log level set to $logLevel")
    }
}