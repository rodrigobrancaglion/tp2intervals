package org.freekode.tp2intervals.integration.platform.strava.activity

/**
 * DTO for the Strava upload status polling response.
 * activity_id is null while the upload is being processed, and populated once complete.
 */
data class ActivityUploadStatusResponseDTO(
    val id: Long,
    val status: String,
    val error: String?,
    val activity_id: Long?,
)
