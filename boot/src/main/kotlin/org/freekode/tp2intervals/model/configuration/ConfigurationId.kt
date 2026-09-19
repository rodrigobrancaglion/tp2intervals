package org.freekode.tp2intervals.model.configuration

import java.io.Serializable

data class ConfigurationId(
    var username: String? = null,
    var key: String? = null
) : Serializable
