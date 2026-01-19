package org.freekode.tp2intervals.infrastructure.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.infrastructure.Signature
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.TPPowerCalculationService
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.TPSettingsResponseDTO
import org.freekode.tp2intervals.infrastructure.utils.Date
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ToIntervalsWorkoutConverter(
    private val tpPowerCalculationService: TPPowerCalculationService
) {
    private val unwantedStepRegex = "^[-*]".toRegex(RegexOption.MULTILINE)

    fun createWorkoutRequestDTO(libraryContainer: LibraryContainer, workout: Workout, settings: TPSettingsResponseDTO): CreateWorkoutRequestDTO {
        val workoutString = getWorkoutString(workout, settings)
        var description = getDescription(workout, workoutString)
        val name: String
        if (workout.details.name.length > 80) {
            name = workout.details.name.substring(0, 76).trim() + "..."
            description = "Name: ${workout.details.name}\n" + description
        } else {
            name = workout.details.name
        }
        val request = CreateWorkoutRequestDTO(
            libraryContainer.externalData.intervalsId.toString(),
            Date.daysDiff(libraryContainer.startDate, workout.date ?: LocalDateTime.now()),
            IntervalsTrainingTypeMapper.getByTrainingType(workout.details.type),
            name,
            workout.details.duration?.seconds,
            workout.details.tssPlanned,
            description,
            null,
        )
        return request
    }

    fun createEventRequestDTO(workout: Workout, settings: TPSettingsResponseDTO): CreateEventRequestDTO {
        val workoutString = getWorkoutString(workout, settings)
        val description = getDescription(workout, workoutString)
        return CreateEventRequestDTO(
            (workout.date ?: LocalDateTime.now()).toString(),
            workout.details.name,
            IntervalsTrainingTypeMapper.getByTrainingType(workout.details.type),
            IntervalsTrainingTypeMapper.getByIntervalsType(workout.details.type.toString()).category.toString(),
            description,
            workout.details.duration?.seconds,
            workout.details.tssPlanned,
        )
    }

    private fun getDescription(workout: Workout, workoutString: String?): String {
        return buildString {
            // Block - Description
            workout.details.description?.takeIf { it.isNotBlank() }?.let {
                val formatted = it.replace(unwantedStepRegex, "`-")
                append("\n- - - -\n${Signature.description}")
                append("#### Description\n\n$formatted\n\n<br>\n\n")
            }

            // Block - Workout Details
            workoutString?.takeIf { it.isNotBlank() }?.let {
                append("\n\n- - - -\n")
                append("#### Workout Details\n$it\n\n")
            }

            // Block - ExternalData (Signature/Links)
            val externalData = workout.details.externalData.toSimpleString()
            if (externalData.isNotBlank()) {
                append(externalData)
            }
        }.trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun getWorkoutString(workout: Workout, settings: TPSettingsResponseDTO): String? {
        // Return null immediately if it's not a workout category
        if (!workout.isWorkoutCategory()) return null

        // Use let for structure if present, otherwise fallback to target intensity
        return workout.structure?.let {
            ToIntervalsStructureConverter(it).toIntervalsStructureStr()
        } ?: tpPowerCalculationService.getTargetIntensity(workout, settings)
    }

}
