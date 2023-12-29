package org.freekode.tp2intervals.infrastructure.intervalsicu

import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutStep
import org.freekode.tp2intervals.domain.workout.WorkoutStepTarget
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class IntervalsWorkoutMapper {
    fun mapToWorkout(eventDTO: IntervalsEventDTO): Workout {
        return Workout(
            eventDTO.start_date_local.toLocalDate(),
            eventDTO.type!!.trainingType,
            eventDTO.name,
            eventDTO.moving_time?.let { Duration.ofSeconds(it) },
            eventDTO.icu_training_load?.toDouble(),
            eventDTO.description,
            eventDTO.workout_doc?.let { mapToWorkoutSteps(it) } ?: listOf(),
            null
        )
    }

    fun mapToActivity(eventDTO: IntervalsActivityDTO): Activity {
        return Activity(
            eventDTO.start_date_local.toLocalDate(),
            eventDTO.type!!.trainingType,
            eventDTO.name,
            eventDTO.moving_time?.let { Duration.ofSeconds(it) },
            eventDTO.icu_training_load?.toDouble(),
        )
    }

    private fun mapToWorkoutSteps(workoutDoc: IntervalsEventDTO.WorkoutDocDTO): List<WorkoutStep> {
        return workoutDoc.steps
            .map {
                if (it.reps != null) {
                    mapMultiStep(it)
                } else {
                    mapSingleStep(it)
                }
            }
    }

    private fun mapMultiStep(stepDTO: IntervalsEventDTO.WorkoutStepDTO): WorkoutStep {
        return WorkoutStep(
            stepDTO.reps!!,
            stepDTO.duration?.let { Duration.ofSeconds(it) } ?: Duration.ZERO,
            listOf(),
            stepDTO.steps!!.map { mapSingleStep(it) }
        )
    }

    private fun mapSingleStep(stepDTO: IntervalsEventDTO.WorkoutStepDTO): WorkoutStep {
        val targets = mutableListOf<WorkoutStepTarget>()

        stepDTO.power
            ?.let {
                mapStepTarget(it, WorkoutStepTarget.TargetType.POWER)
            }?.also { targets.add(it) }
        stepDTO.cadence
            ?.let {
                mapStepTarget(it, WorkoutStepTarget.TargetType.CADENCE)
            }?.also { targets.add(it) }

        return WorkoutStep(
            1,
            stepDTO.duration?.let { Duration.ofSeconds(it) } ?: Duration.ZERO,
            targets,
            listOf()
        )
    }

    private fun mapStepTarget(stepValueDTO: IntervalsEventDTO.StepValueDTO, targetType: WorkoutStepTarget.TargetType) =
        WorkoutStepTarget(
            targetType,
            mapTargetUnit(stepValueDTO.units),
            mapTargetValue(stepValueDTO)
        )

    private fun mapTargetUnit(units: String): WorkoutStepTarget.TargetUnit =
        when (units) {
            "%ftp" -> WorkoutStepTarget.TargetUnit.FTP_PERCENTAGE
            "rpm" -> WorkoutStepTarget.TargetUnit.CADENCE
            else -> throw RuntimeException("unknown unit $units")
        }

    private fun mapTargetValue(stepValueDTO: IntervalsEventDTO.StepValueDTO): WorkoutStepTarget.TargetValue =
        if (stepValueDTO.value != null) {
            WorkoutStepTarget.TargetValue(stepValueDTO.value)
        } else {
            WorkoutStepTarget.TargetValue(stepValueDTO.start!!, stepValueDTO.end!!)
        }
}
