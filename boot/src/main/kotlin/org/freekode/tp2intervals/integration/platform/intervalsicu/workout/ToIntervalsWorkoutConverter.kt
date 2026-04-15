package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.PlatformTrainingMapper
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto.IntervalsAthleteProfileDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.EventRequestDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.WorkoutRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPPowerCalculationService
import org.freekode.tp2intervals.integration.utils.Date
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ToIntervalsWorkoutConverter(
    private val tpPowerCalculationService: TPPowerCalculationService
) {
    private val unwantedStepRegex = "^[-*]".toRegex(RegexOption.MULTILINE)

    fun createWorkoutRequestDTO(libraryContainer: LibraryContainer, workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): WorkoutRequestDTO {
        val workoutString = getWorkoutString(workout, athleteProfileDTO)
        var description = getDescription(workout, workoutString)
        val name: String
        if (workout.details.name.length > 80) {
            name = workout.details.name.substring(0, 76).trim() + "..."
            description = "Name: ${workout.details.name}\n" + description
        } else {
            name = workout.details.name
        }

        val typeTraining = PlatformTrainingMapper.mapTpToIntervals(workout.details)

        val request = WorkoutRequestDTO(
            libraryContainer.externalData.intervalsId.toString(),
            Date.daysDiff(libraryContainer.startDate, workout.date ?: LocalDateTime.now()),
            name,
            typeTraining.category,
            typeTraining.typeName,
            description,
            workout.details.duration?.seconds,
            workout.details.tssPlanned,
            null,
        )
        return request
    }

    fun createEventRequestDTO(workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): EventRequestDTO {
        val workoutString = getWorkoutString(workout, athleteProfileDTO)
        val description = getDescription(workout, workoutString)

        val typeTraining = PlatformTrainingMapper.mapTpToIntervals(workout.details)

        return EventRequestDTO(
            (workout.date ?: LocalDateTime.now()).toString(),
            workout.details.name,
            typeTraining.category,
            typeTraining.typeName,
            description,
            workout.details.duration?.seconds,
            workout.details.tssPlanned,
            null,
            null
        )
    }

    private fun getDescription(workout: Workout, workoutString: String?): String {
        return buildString {
            // Block - Description
            workout.details.description?.takeIf { it.isNotBlank() }?.let {
                val formatted = it.replace(unwantedStepRegex, "`-")
//                append("\n- - - -\n${Signature.description}")
                append("# Description\n\n$formatted\n\n<br>\n\n")
            }

            // Block - Workout Details
            workoutString?.takeIf { it.isNotBlank() }?.let {
//                append("\n\n- - - -\n")
                append("# Workout Details\n$it\n\n")
            }

            // Block - ExternalData (Signature/Links)
            val externalData = workout.details.externalData.toSimpleString()
            if (externalData.isNotBlank()) {
                append(externalData)
            }
        }.trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun getWorkoutString(workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): String {
        // Use let for structure if present, otherwise fallback to target intensity
        return workout.structure?.let {
            ToIntervalsStructureConverter(it).toIntervalsStructureStr()
        } ?: tpPowerCalculationService.getTargetIntensity(workout, athleteProfileDTO)
    }

}
