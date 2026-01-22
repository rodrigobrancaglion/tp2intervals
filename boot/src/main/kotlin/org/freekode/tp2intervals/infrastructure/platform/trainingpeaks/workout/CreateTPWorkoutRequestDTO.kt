package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.workout.Workout
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

class CreateTPWorkoutRequestDTO(
    var athleteId: String,
    var workoutDay: LocalDate,
    var workoutTypeValueId: Int,
    var title: String,
    var description: String?,
    var totalTime: Double?,
    var totalTimePlanned: Double?,
    var tssActual: Int?,
    var tssPlanned: Int?,
    val ifPlanned: Double?,
    var structure: String?,
    val startTime: LocalDateTime?,
) {

    companion object {

        fun planWorkout(
            athleteId: String, workout: Workout, structureStr: String?
        ): CreateTPWorkoutRequestDTO {
            return CreateTPWorkoutRequestDTO(
                athleteId,
                workout.date?.toLocalDate() ?: LocalDate.now(),
                TPTrainingTypeMapper.getByType(workout.details.type),
                workout.details.name,
                workout.details.externalData.toSimpleString(),
                null,
                workout.details.duration?.toMinutes()?.toDouble()?.div(60),
                null,
                workout.details.tssPlanned,
                convertIcuIntensityToIf(workout.details.ifPlanned),
                structureStr,
                workout.date ?: LocalDateTime.now(),
            )
        }

        fun convertIcuIntensityToIf(icuIntensity: Double?): Double? {
            if (icuIntensity == null) return null

            return BigDecimal(icuIntensity / 100.0)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        }

    }
}
