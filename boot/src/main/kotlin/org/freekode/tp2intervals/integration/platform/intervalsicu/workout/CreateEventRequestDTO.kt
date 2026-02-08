package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

data class CreateEventRequestDTO(
    val start_date_local: String?,
    val name: String?,
    val category: String?,
    val type: String?,
    val description: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
)
