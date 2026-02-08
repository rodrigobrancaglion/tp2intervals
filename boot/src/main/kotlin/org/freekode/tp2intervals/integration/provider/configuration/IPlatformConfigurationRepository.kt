package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest

interface IPlatformConfigurationRepository {
    fun platform(): Platform

    fun updateConfig(request: UpdateConfigurationRequest)
}
