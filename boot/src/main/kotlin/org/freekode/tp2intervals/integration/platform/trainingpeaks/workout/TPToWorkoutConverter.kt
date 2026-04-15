package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.workout.Attachment
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.integration.platform.trainingpeaks.library.dto.TPWorkoutLibraryItemDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.mapper.TPTrainingFeelingMapper
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.TPActivityRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.TPBaseWorkoutDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.TPNoteResponseDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.TPWorkoutCalendarDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.structure.FromTPStructureConverter
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.structure.TPWorkoutStructureDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class TPToWorkoutConverter(
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun toWorkout(tpWorkout: TPWorkoutCalendarDTO, attachments: List<Attachment> = listOf()): Workout {
        val dateTime = if (!tpWorkout.startTime.isNullOrBlank()) {
            // Keeps Date + Hours + Minutes
            LocalDateTime.parse(tpWorkout.startTime)
        } else {
            // Takes the date and sets time to midnight (00:00)
            tpWorkout.workoutDay.toLocalDate().atStartOfDay()
        }

        return toWorkout(tpWorkout, dateTime, attachments)
    }

    fun toWorkout(tpWorkout: TPWorkoutLibraryItemDTO, attachments: List<Attachment> = listOf()): Workout {
        return toWorkout(tpWorkout, LocalDate.now().atStartOfDay(), attachments)
    }

    fun toWorkout(tpNote: TPNoteResponseDTO): Workout {
        return Workout.note(
            tpNote.noteDate,
            tpNote.title,
            tpNote.description,
            ExternalData.empty().withTrainingPeaks(tpNote.id.toString())
        )
    }

    private fun toWorkout(tpWorkout: TPBaseWorkoutDTO<TPWorkoutStructureDTO>, workoutDate: LocalDateTime, attachments: List<Attachment>): Workout {
        val workoutsStructure = toWorkoutStructure(tpWorkout)

        val description = tpWorkout.description.orEmpty()
        //description += tpWorkout.coachComments?.let { "\n- - - -\n$it" }.orEmpty()

        val capitalizedTitle = if (tpWorkout.title.isNullOrBlank()) "Workout"
        else tpWorkout.title.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }

        return Workout(tpWorkout.workoutId,
            WorkoutDetails(
                type = tpWorkout.getWorkoutTypeFromId(),
                workoutSubTypeId = tpWorkout.getWorkoutSubTypeFromId(),
                name = capitalizedTitle,
                description = description,
                duration = tpWorkout.totalTimePlanned?.let { Duration.ofMinutes((it * 60).toLong()) },
                tssPlanned = tpWorkout.tssPlanned,
                ifPlanned = tpWorkout.ifPlanned,
                externalData = getWorkoutExternalData(tpWorkout),
                attachments
            ),
            workoutDate,
            workoutsStructure,
        )
    }

    private fun toWorkoutStructure(tpWorkout: TPBaseWorkoutDTO<TPWorkoutStructureDTO>) =
        try {
            if (tpWorkout.structure?.structure.isNullOrEmpty()) {
                throw IllegalArgumentException("There is no structure")
            }
            FromTPStructureConverter.toWorkoutStructure(tpWorkout.structure!!)
        } catch (e: IllegalArgumentException) {
            log.warn("Error during TP Workout conversion, skipping, id: ${tpWorkout.workoutId}, name: ${tpWorkout.title}, error - ${e.message}'")
            null
        }

    private fun getWorkoutExternalData(tpWorkout: TPBaseWorkoutDTO<TPWorkoutStructureDTO>): ExternalData {
        return ExternalData.empty().withTrainingPeaks(tpWorkout.workoutId.toString()).fromSimpleString(tpWorkout.description ?: "")
    }

    fun toActivityDomain(tpWorkout: TPBaseWorkoutDTO<TPWorkoutStructureDTO>): Activity {
        return Activity(
            tpWorkout.workoutId,
            tpWorkout.workoutDay,
            tpWorkout.mapType(),
            tpWorkout.title,
            tpWorkout.description,
            null,
            null,
            tpWorkout.rpe,
            tpWorkout.feeling,
        )
    }

    fun convertToPutRequest(activity: Activity, workout: TPWorkoutCalendarDTO, types: List<BaseType>): TPActivityRequestDTO {
        // Check if "RPE" is on the list of allowed update types.
        if (types.contains(ActivityType.RPE)) {
            workout.rpe = activity.rpe
        }

        // Check if "FEEL" is on the list of allowed update types.
        if (types.contains(ActivityType.FEEL)) {
            val feelingType = TPTrainingFeelingMapper.getByICUValue(activity.feel)
            workout.feeling = TPTrainingFeelingMapper.getTPValue(feelingType)
        }

        val structureJsonString = workout.structure?.let {
            objectMapper.writeValueAsString(it)
        }

        return TPActivityRequestDTO(workout, structureJsonString)
    }

}
