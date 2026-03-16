package org.freekode.tp2intervals.integration.platform.strava.activity

/**
 * DTO for the initial Strava upload response (before processing completes).
 */
data class ActivityUploadResponseDTO(
    val id: Long,
    val status: String,
    val error: String?,
    val activity_id: Long?,
)
