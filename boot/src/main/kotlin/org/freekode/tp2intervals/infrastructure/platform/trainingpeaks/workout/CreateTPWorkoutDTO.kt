package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout

import java.time.LocalDateTime
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.structure.ExternalDataConverter

class CreateTPWorkoutDTO(
    var athleteId: String,
    var workoutDay: LocalDateTime,
    var workoutTypeValueId: Int,
    var title: String,
    var description: String?,
    var totalTime: Double?,
    var totalTimePlanned: Double?,
    var tssActual: Int?,
    var tssPlanned: Int?,
    var structure: String?
) {

    companion object {

        fun planWorkout(
            athleteId: String, workout: Workout, structureStr: String?
        ): CreateTPWorkoutDTO {
            return CreateTPWorkoutDTO(
                athleteId,
                workout.date.atStartOfDay(),
                TPWorkoutTypeMapper.getByType(workout.type),
                workout.name,
                workoutDescriptionBuilder(workout),
                null,
                workout.duration?.toMinutes()?.toDouble()?.div(60),
                null,
                workout.load,
                structureStr
            )
        }

        fun createActivity(athleteId: String, activity: Activity): CreateTPWorkoutDTO {
            return CreateTPWorkoutDTO(
                athleteId,
                activity.startedAt,
                TPWorkoutTypeMapper.getByType(activity.type),
                activity.title,
                null,
                activity.duration.toMinutes().toDouble().div(60),
                null,
                activity.load,
                null,
                null
            )
        }

        private fun workoutDescriptionBuilder(workout: Workout): String {
            return ExternalDataConverter().toSimpleString(workout.externalData)
        }
    }
}
