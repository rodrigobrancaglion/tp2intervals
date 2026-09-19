package org.freekode.tp2intervals.model.configuration

import jakarta.persistence.*

@IdClass(ConfigurationId::class)
@Table(name = "config")
@Entity
data class ConfigurationEntity(
    @Id
    @Column(name = "username")
    var username: String?,

    @Id
    @Column(name = "`key`", nullable = false)
    var key: String?,

    @Column(name = "value")
    var value: String?,
) {
    constructor() : this(null, null, null)
}