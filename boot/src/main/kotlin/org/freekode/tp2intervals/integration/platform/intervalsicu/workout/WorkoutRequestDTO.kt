package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

data class WorkoutRequestDTO(
    val folder_id: String,
    val day: Int,
    val name: String?,
    val category: String?,
    val type: String?,
    val description: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
    val file_contents: String?,
)
