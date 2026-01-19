package org.freekode.tp2intervals.infrastructure.platform.intervalsicu.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.activity.ActivityRepository
import org.freekode.tp2intervals.infrastructure.platform.intervalsicu.IntervalsApiClient
import org.freekode.tp2intervals.infrastructure.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.infrastructure.platform.intervalsicu.workout.IntervalsWorkoutRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IntervalsActivityRepository(
    private val intervalsApiClient: IntervalsApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
    private val intervalsWorkoutRepository: IntervalsWorkoutRepository
) : ActivityRepository {
    override fun platform() = Platform.INTERVALS

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        activities.forEach { activity ->

            val newActivityDTO = IntervalsToActivityConverter().toDTO(activity, types)

            intervalsApiClient.updateActivity(
                activity.workoutId.toString(),
                newActivityDTO
            )
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val activities =
            intervalsApiClient.getActivities(
                intervalsConfigurationRepository.getConfiguration().athleteId,
                startDate.atStartOfDay().toString(),
                endDate.atStartOfDay().plusDays(1).minusSeconds(1).toString()
            )

        val workoutsMap = intervalsWorkoutRepository.getWorkoutsFromCalendar(startDate, endDate)
            .associateBy { it.id }

        return activities
            .map {
                val pairedWorkout = workoutsMap[it.paired_event_id]
                IntervalsToActivityConverter().toDomain(it, pairedWorkout) }
    }
}
