package org.freekode.tp2intervals.integration.platform.trainerroad.workout

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.domain.workout.structure.*
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRWorkoutDetailsDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRWorkoutResponseDTO
import java.time.Duration

class TRWorkoutMapper {
    fun toWorkout(trWorkoutResponseDTO: TRWorkoutResponseDTO, removeHtmlTags: Boolean): Workout {
        val trWorkout: TRWorkoutResponseDTO.TRWorkout = trWorkoutResponseDTO.workout
        val steps = convertSteps(trWorkout.intervalData)
        return Workout(
            toWorkoutDetails(trWorkout.details, removeHtmlTags),
            null,
            steps.takeIf { it.isNotEmpty() }
                ?.let { WorkoutStructure(WorkoutStructure.TargetUnit.FTP_PERCENTAGE, it) },
        )
    }

    fun toWorkoutDetails(detailsDTO: TRWorkoutDetailsDTO, removeHtmlTags: Boolean): WorkoutDetails {
        return WorkoutDetails(
            type = if (detailsDTO.isOutside) TrainingType.BIKE else TrainingType.VIRTUAL_BIKE,
            name = detailsDTO.workoutName,
            description = getDescription(detailsDTO.workoutDescription, removeHtmlTags),
            duration = Duration.ofMinutes(detailsDTO.duration.toLong()),
            tssPlanned = detailsDTO.tss,
            ifPlanned = null,
            externalData = ExternalData.empty().withTrainerRoad(detailsDTO.id),
            attachments = listOf()
        )
    }

    private fun convertSteps(intervals: List<TRWorkoutResponseDTO.IntervalsDataDTO>): List<WorkoutStep> {
        val steps = mutableListOf<WorkoutStep>()

        for (interval in intervals) {
            if (interval.name == "Workout") {
                continue
            }
            val stepLength = StepLength.seconds((interval.end - interval.start).toLong())

            val name = if (interval.name == "Fake") "Step" else interval.name

            val singleStep =
                SingleStep(name, stepLength, StepTarget(interval.targetStart(), interval.targetEnd()), null, false)
            steps.add(singleStep)
        }
        return steps
    }

    private fun getDescription(description: String, removeHtmlTags: Boolean): String =
        if (removeHtmlTags) {
            description.replace("<[^>]*>".toRegex(), " ").replace("&" + "nbsp;", " ").replace("\\s+".toRegex(), " ")
        } else {
            description
        }.trim()
}
