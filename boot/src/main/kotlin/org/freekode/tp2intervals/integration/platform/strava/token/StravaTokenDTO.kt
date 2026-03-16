package org.freekode.tp2intervals.integration.platform.strava.token

/**
 * DTO for the Strava OAuth2 token response.
 * expires_at is a Unix timestamp; expires_in is the TTL in seconds (typically 21600 = 6h).
 */
data class StravaTokenDTO(
    val token_type: String,
    val access_token: String,
    val refresh_token: String,
    val expires_at: Long,
    val expires_in: Long,
)
