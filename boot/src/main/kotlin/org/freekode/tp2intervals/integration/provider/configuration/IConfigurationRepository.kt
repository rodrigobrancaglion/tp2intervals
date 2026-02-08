package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.domain.config.AppConfiguration
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest

interface IConfigurationRepository {
    fun getConfiguration(key: String): String?

    fun getConfigurations(): AppConfiguration

    fun getConfigurationByPrefix(prefix: String): AppConfiguration

    fun updateConfig(request: UpdateConfigurationRequest)
}