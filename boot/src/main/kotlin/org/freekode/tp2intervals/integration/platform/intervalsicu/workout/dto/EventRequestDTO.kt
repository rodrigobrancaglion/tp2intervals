package org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto

data class EventRequestDTO(
    val start_date_local: String?,
    val name: String?,
    val category: String?,
    val type: String?,
    val description: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
    val attachments: AttachmentDTO?,
    val paired_activity_id: String?,
) {
    class AttachmentDTO(
        val id: String,
        val filename: String,
        val mimetype: String,
        val url: String,
    )
}
