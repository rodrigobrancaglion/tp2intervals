package org.freekode.tp2intervals.integration.platform.wahoo.token

data class WahooTokenDTO(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val scope: String,
    val created_at: Long
)
