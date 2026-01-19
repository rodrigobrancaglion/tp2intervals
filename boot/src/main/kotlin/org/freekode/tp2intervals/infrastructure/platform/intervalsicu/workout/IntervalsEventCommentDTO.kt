package org.freekode.tp2intervals.infrastructure.platform.intervalsicu.workout

data class IntervalsEventCommentDTO(
    val athlete_id: Long,
    val content: String,
    val to_activity_id: String,
)