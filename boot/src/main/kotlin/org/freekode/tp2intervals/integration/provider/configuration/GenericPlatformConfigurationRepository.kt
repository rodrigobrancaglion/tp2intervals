package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.springframework.stereotype.Repository

@Repository
class GenericPlatformConfigurationRepository(
    private val iConfigurationRepository: IConfigurationRepository,
    ) : IPlatformConfigurationRepository {
    override fun platform() = Platform.GENERIC

    override fun updateConfig(request: UpdateConfigurationRequest) {
        iConfigurationRepository.updateConfig(UpdateConfigurationRequest(request.getByPrefix(Platform.GENERIC.key)))
    }
}
