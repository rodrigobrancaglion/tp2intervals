package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.PlatformTrainingMapper
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto.IntervalsAthleteProfileDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class IcuEventConverter(
    private val icuWorkoutConverter: IcuWorkoutConverter
) {

    fun createIcuEventEx(workout: Workout, athleteProfileDTO: IntervalsAthleteProfileDTO): IcuEventEx {
        val workoutString = icuWorkoutConverter.getWorkoutString(workout, athleteProfileDTO)
        val description = icuWorkoutConverter.getDescription(workout, workoutString)

        val typeTraining = PlatformTrainingMapper.mapTpToIntervals(workout.details)

        return IcuEventEx(
            name = workout.details.name,
            description = description,
            start_date_local = (workout.date ?: LocalDateTime.now()).toString(),
            category = typeTraining.category,
            type = typeTraining.typeName,
            moving_time = workout.details.duration?.seconds,
            icu_training_load = workout.details.tssPlanned
        )
    }

}
