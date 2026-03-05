package org.freekode.tp2intervals.integration.platform.myfitnesspal.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.AppConfiguration

data class MfpConfiguration(
    val sessionToken: String?,
    val nextAuthToken: String?,
    val rememberMe: String?,
    val userId: String?,
    val username: String?,
) {
    companion object {
        val sessionTokenKey = "${Platform.MYFITNESSPAL.key}.session-cookie"
        val nextAuthTokenKey = "${Platform.MYFITNESSPAL.key}.session-token-cookie"
        val rememberMeKey = "${Platform.MYFITNESSPAL.key}.remember-me-cookie"
        val userIdKey = "${Platform.MYFITNESSPAL.key}.user-id"
        val usernameKey = "${Platform.MYFITNESSPAL.key}.username"
    }

    constructor(appConfiguration: AppConfiguration) : this(appConfiguration.configMap)

    constructor(map: Map<String, String?>) : this(
        map[sessionTokenKey],
        map[nextAuthTokenKey],
        map[rememberMeKey],
        map[userIdKey],
        map[usernameKey],
    )

    fun isValid() = !username.isNullOrBlank() && !userId.isNullOrBlank()

    fun toCookieMap(): Map<String, String> {
        val cookies = mutableMapOf<String, String>()
        if (!sessionToken.isNullOrBlank()) cookies["_mfp_session"] = sessionToken
        if (!nextAuthToken.isNullOrBlank()) cookies["__Secure-next-auth.session-token"] = nextAuthToken
        if (!rememberMe.isNullOrBlank()) cookies["remember_me"] = rememberMe
        return cookies
    }
}
