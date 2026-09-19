package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto

import org.freekode.tp2intervals.domain.workout.WorkoutComment
import java.time.ZonedDateTime

data class TPWorkoutCommentDTO(
    val id: Long,
    val comment: String?,
    val dateCreated: ZonedDateTime?, // ISO date format "2026-01-14T21:16:53Z"
    val workoutId: Long?,
    val commenterPersonId: Long?,
    val firstName: String?,
    val lastName: String?,
    val commenterName: String?,
    val isCoach: Boolean
) {
    fun toDomain(): WorkoutComment {
        return WorkoutComment(
            id = this.id,
            comment = this.comment,
            dateCreated = this.dateCreated,
            workoutId = this.workoutId,
            commenterPersonId = this.commenterPersonId,
            firstName = this.firstName,
            lastName = this.lastName,
            commenterName = this.commenterName,
            isCoach = this.isCoach
        )
    }
}