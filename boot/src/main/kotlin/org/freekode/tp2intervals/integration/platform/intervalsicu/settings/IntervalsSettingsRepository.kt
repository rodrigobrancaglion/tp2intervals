package org.freekode.tp2intervals.integration.platform.intervalsicu.settings

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IntervalsWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["tpSettingsCache"])
@Repository
class IntervalsSettingsRepository(
    private val intervalsWorkoutApiClient: IntervalsWorkoutApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,

    objectMapper: ObjectMapper

) : ISettingsRepository {
    override fun platform() = Platform.INTERVALS

    override fun save(activities: List<Activity>, types: List<BaseType>) {
//        activities.forEach { activity ->
//
//            val newActivityDTO = IntervalsToActivityConverter().toDTO(activity, types)
//
//            intervalsApiClient.updateActivity(
//                activity.workoutId.toString(),
//                newActivityDTO
//            )
//        }
    }

    override fun get(startDate: LocalDate, endDate: LocalDate): List<Activity> {
//        val activities =
//            intervalsApiClient.getActivities(
//                intervalsConfigurationRepository.getConfiguration().athleteId,
//                startDate.atStartOfDay().toString(),
//                endDate.atStartOfDay().plusDays(1).minusSeconds(1).toString()
//            )
//
//        val workoutsMap = intervalsWorkoutRepository.getWorkoutsFromCalendar(startDate, endDate)
//            .associateBy { it.id }
//
//        return activities
//            .map {
//                val pairedWorkout = workoutsMap[it.paired_event_id]
//                IntervalsToActivityConverter().toDomain(it, pairedWorkout) }
        return emptyList()
    }

}
