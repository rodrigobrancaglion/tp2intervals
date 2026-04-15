package org.freekode.tp2intervals.integration.platform.strava.activity.dto

/**
 * DTO for the initial Strava upload response (before processing completes).
 */
data class StravaActivityUploadResponseDTO(
    val id: Long,
    val status: String,
    val error: String?,
    val activity_id: Long?,
)
