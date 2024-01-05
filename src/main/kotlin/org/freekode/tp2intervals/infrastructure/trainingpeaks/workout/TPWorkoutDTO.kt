package org.freekode.tp2intervals.infrastructure.trainingpeaks.workout

import org.freekode.tp2intervals.infrastructure.trainingpeaks.workout.structure.TPWorkoutStructureDTO
import java.time.LocalDateTime

class TPWorkoutDTO(
    var workoutId: String,
    var workoutDay: LocalDateTime,
    var workoutTypeValueId: Int?,
    var title: String,
    var totalTimePlanned: Double?,
    var tssPlanned: Double?,
    var description: String?,
    var coachComments: String?,
    var structure: TPWorkoutStructureDTO?
) {
    fun getWorkoutType() = workoutTypeValueId?.let { TPWorkoutType.findByValue(it) }
}
