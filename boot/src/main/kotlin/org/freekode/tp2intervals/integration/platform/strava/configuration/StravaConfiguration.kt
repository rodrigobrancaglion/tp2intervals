package org.freekode.tp2intervals.integration.platform.strava.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.AppConfiguration

/**
 * Strava platform configuration holding OAuth2 credentials stored in the DB.
 */
data class StravaConfiguration(
    val clientId: String?,
    val clientSecret: String?,
    val refreshToken: String?,
) {
    companion object {
        val clientIdKey = "${Platform.STRAVA.key}.client-id"
        val clientSecretKey = "${Platform.STRAVA.key}.client-secret"
        val refreshTokenKey = "${Platform.STRAVA.key}.refresh-token"
    }

    constructor(appConfiguration: AppConfiguration) : this(appConfiguration.configMap)

    constructor(map: Map<String, String?>) : this(
        map[clientIdKey],
        map[clientSecretKey],
        map[refreshTokenKey],
    )

    fun isValid() = !clientId.isNullOrBlank() &&
        !clientSecret.isNullOrBlank() &&
        !refreshToken.isNullOrBlank()
}
