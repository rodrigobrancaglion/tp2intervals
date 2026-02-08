package org.freekode.tp2intervals.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app")
class DefaultConfiguration(
    val defaultConfig: Map<String, String>?,
)
