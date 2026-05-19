package org.freekode.tp2intervals.integration.platform.wahoo.configuration

import org.freekode.tp2intervals.domain.config.AppConfiguration

data class WahooConfiguration(
    val clientId: String?,
    val clientSecret: String?,
    val refreshToken: String?,
) {
    constructor(appConfiguration: AppConfiguration) : this(
        clientId = appConfiguration.find("wahoo.client-id"),
        clientSecret = appConfiguration.find("wahoo.client-secret"),
        refreshToken = appConfiguration.find("wahoo.refresh-token"),
    )

    fun isValid(): Boolean {
        return !clientId.isNullOrBlank() && !clientSecret.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }
}
