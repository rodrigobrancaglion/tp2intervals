package org.freekode.tp2intervals.domain.workout

import java.time.ZonedDateTime

data class WorkoutComment(
    val id: Long,
    val comment: String?,
    val dateCreated: ZonedDateTime?, // ISO date format "2026-01-14T21:16:53Z"
    val workoutId: Long?,
    val commenterPersonId: Long?,
    val firstName: String?,
    val lastName: String?,
    val commenterName: String?,
    val isCoach: Boolean
)