package org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto

data class IntervalsEventCommentDTO(
    val athlete_id: Long,
    val content: String,
    val to_activity_id: String,
)