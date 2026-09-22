package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.model.configuration.ConfigurationEntity
import org.freekode.tp2intervals.model.configuration.ConfigurationId
import org.springframework.data.repository.CrudRepository

interface IConfigurationCrudRepository : CrudRepository<ConfigurationEntity, ConfigurationId> {

    fun findByUsername(username: String): List<ConfigurationEntity>

    fun findByUsernameAndKeyLike(username: String, prefix: String): List<ConfigurationEntity>

    fun deleteByUsername(username: String)
}
