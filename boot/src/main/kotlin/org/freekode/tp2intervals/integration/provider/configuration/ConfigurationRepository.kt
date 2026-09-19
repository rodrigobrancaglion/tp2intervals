package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.domain.config.AppConfiguration
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.model.configuration.ConfigurationEntity
import org.freekode.tp2intervals.model.configuration.ConfigurationId
import org.freekode.tp2intervals.utils.UserContextHolder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ConfigurationRepository(
    private val iConfigurationCrudRepository: IConfigurationCrudRepository,
) : IConfigurationRepository {

    override fun getConfiguration(key: String): String? {
        val id = ConfigurationId(UserContextHolder.username, key)
        return iConfigurationCrudRepository.findByIdOrNull(id)?.value
    }

    override fun getConfigurations(): AppConfiguration {
        val username = UserContextHolder.username
        return toDomain(iConfigurationCrudRepository.findByUsername(username))
    }

    override fun getConfigurationByPrefix(prefix: String): AppConfiguration {
        val username = UserContextHolder.username
        return toDomain(iConfigurationCrudRepository.findByUsernameAndKeyLike(username, "$prefix%"))
    }

    override fun updateConfig(request: UpdateConfigurationRequest) {
        val username = UserContextHolder.username
        request.config.forEach { (key, value) ->
            val id = ConfigurationId(username, key)
            if (value == null) {
                iConfigurationCrudRepository.deleteById(id)
            } else {
                iConfigurationCrudRepository.save(ConfigurationEntity(username, key, value))
            }
        }
    }

    private fun toDomain(entities: Iterable<ConfigurationEntity>): AppConfiguration {
        val configMap = entities.associateBy { it.key!! }.mapValues { it.value.value!! }
        return AppConfiguration(configMap)
    }
}
