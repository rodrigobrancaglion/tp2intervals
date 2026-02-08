package org.freekode.tp2intervals.integration.provider.configuration

import org.freekode.tp2intervals.model.configuration.ConfigurationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IConfigurationCrudRepository : CrudRepository<ConfigurationEntity, String> {

    fun findByKeyLike(prefix: String): List<ConfigurationEntity>
}
