package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IntervalsWorkoutRepository
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IntervalsActivityRepository(
    private val intervalsActivityApiClient: IntervalsActivityApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
    private val intervalsWorkoutRepository: IntervalsWorkoutRepository
) : IActivityRepository {
    override fun platform() = Platform.INTERVALS

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        activities.forEach { activity ->

            val newActivityDTO = IntervalsToActivityConverter().toDTO(activity, types)

            intervalsActivityApiClient.updateActivity(
                activity.workoutId.toString(),
                newActivityDTO
            )
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val activities =
            intervalsActivityApiClient.getActivities(
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
