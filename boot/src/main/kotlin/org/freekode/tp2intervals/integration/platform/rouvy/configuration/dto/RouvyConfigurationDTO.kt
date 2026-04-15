package org.freekode.tp2intervals.integration.platform.rouvy.configuration.dto

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.AppConfiguration
import org.freekode.tp2intervals.integration.PlatformException

data class RouvyConfigurationDTO(
    val email: String,
    val password: String,
) {
    companion object Companion {
        private val emailConfigKey = "${Platform.ROUVY.key}.email"
        private val passwordConfigKey = "${Platform.ROUVY.key}.password"
    }

    constructor(appConfiguration: AppConfiguration) : this(appConfiguration.configMap)

    constructor(map: Map<String, String?>) : this(
        map[emailConfigKey] ?: "",
        map[passwordConfigKey] ?: "",
    ) {
        if (map[emailConfigKey].isNullOrBlank() || map[passwordConfigKey].isNullOrBlank()) {
             throw PlatformException(
                 Platform.ROUVY,
                 "Rouvy access is not fully configured (email and password required)"
             )
        }
    }
}