package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.domain.config.AppConfiguration
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.model.configuration.ConfigurationEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ConfigurationRepository(
    private val iConfigurationCrudRepository: IConfigurationCrudRepository,
) : IConfigurationRepository {

    override fun getConfiguration(key: String): String? {
        return iConfigurationCrudRepository.findByIdOrNull(key)?.value
    }

    override fun getConfigurations(): AppConfiguration {
        return toDomain(iConfigurationCrudRepository.findAll())
    }

    override fun getConfigurationByPrefix(prefix: String): AppConfiguration {
        return toDomain(iConfigurationCrudRepository.findByKeyLike("$prefix%"))
    }

    override fun updateConfig(request: UpdateConfigurationRequest) {
        request.config.forEach { (key, value) ->
            if (value == null) {
                iConfigurationCrudRepository.deleteById(key)
            } else {
                iConfigurationCrudRepository.save(ConfigurationEntity(key, value))
            }
        }
    }

    private fun toDomain(entities: Iterable<ConfigurationEntity>): AppConfiguration {
        val configMap = entities.associateBy { it.key!! }.mapValues { it.value.value!! }
        return AppConfiguration(configMap)
    }
}
