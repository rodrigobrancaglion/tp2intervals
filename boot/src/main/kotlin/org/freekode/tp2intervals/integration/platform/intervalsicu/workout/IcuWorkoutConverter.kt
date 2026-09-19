package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.PlatformTrainingMapper
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.domain.workout.structure.*
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto.IntervalsAthleteProfileDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IcuWorkoutDocDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IcuWorkoutEx
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPPowerCalculationService
import org.freekode.tp2intervals.integration.utils.Date
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class IcuWorkoutConverter(
    private val tpPowerCalculationService: TPPowerCalculationService
) {

    private val unwantedStepRegex = "^[-*]".toRegex(RegexOption.MULTILINE)

    fun createIcuWorkoutEx(libraryContainer: LibraryContainer, workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): IcuWorkoutEx {
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

        val request = IcuWorkoutEx(
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

    fun toWorkout(icuEventEx: IcuEventEx): Workout {
        val workoutsStructure = toWorkoutStructure(icuEventEx)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val startDateLocal = LocalDateTime.parse(icuEventEx.start_date_local, formatter)

        return Workout(
            icuEventEx.id,
            WorkoutDetails(
                type = icuEventEx.getTrainingType(),
                workoutSubTypeId = icuEventEx.getSubType(),
                name = icuEventEx.name,
                description = icuEventEx.description,
                duration = icuEventEx.mapDuration(),
                tssPlanned = icuEventEx.icu_training_load,
                ifPlanned = icuEventEx.getIntensityFactor(),
                externalData = ExternalData.empty().withIntervals(icuEventEx.id.toString()).fromSimpleString(icuEventEx.description ?: "")
            ),
            startDateLocal,
            workoutsStructure,
        )
    }

    var paramWorkoutDoc: IcuWorkoutDocDTO? = null

    private fun toWorkoutStructure(icuEventEx: IcuEventEx): WorkoutStructure? {
        return icuEventEx.workout_doc?.let { workoutDoc ->
            if (workoutDoc.steps.isNotEmpty()) {
                WorkoutStructure(
                    workoutDoc.mapTarget(),
                    mapToWorkoutSteps(workoutDoc)
                )
            } else {
                null
            }
        }
    }

    private fun mapToWorkoutSteps(workoutDoc: IcuWorkoutDocDTO): List<WorkoutStep> {
        paramWorkoutDoc = workoutDoc
        return workoutDoc.steps.map {
            if (it.reps != null) {
                mapMultiStep(it)
            } else {
                mapSingleStep(it)
            }
        }
    }

    private fun mapMultiStep(
        stepDTO: IcuWorkoutDocDTO.WorkoutStepDTO
    ): MultiStep {
        return MultiStep(
            stepDTO.text,
            stepDTO.reps!!,
            stepDTO.steps!!.map { mapSingleStep(it) }
        )
    }

    private fun mapSingleStep(
        stepDTO: IcuWorkoutDocDTO.WorkoutStepDTO
    ): SingleStep {
        val targetMapper = IcuTargetConverter(
            paramWorkoutDoc?.ftp?.toDouble(),
            paramWorkoutDoc?.lthr?.toDouble(),
            paramWorkoutDoc?.threshold_pace?.toDouble()
        )
        val mainTarget = targetMapper.toMainTarget(stepDTO)
        val cadenceTarget = stepDTO.cadence?.let { targetMapper.toCadenceTarget(it) }

        return SingleStep(
            stepDTO.text,
            stepDTO.notes,
            getStepLength(stepDTO),
            mainTarget,
            cadenceTarget,
            stepDTO.ramp == true
        )
    }

    private fun getStepLength(stepDTO: IcuWorkoutDocDTO.WorkoutStepDTO) =
        if (stepDTO.distance != null) {
            StepLength.meters(stepDTO.distance)
        } else {
            StepLength.seconds(stepDTO.duration ?: 600)
        }

    fun getWorkoutString(workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): String {
        // Use let for structure if present, otherwise fallback to target intensity
        return workout.structure?.let {
            IcuStructureConverter(it).toIntervalsStructureStr()
        } ?: tpPowerCalculationService.getTargetIntensity(workout, athleteProfileDTO)
    }

    fun getDescription(workout: Workout, workoutString: String?): String {
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
}
