package org.freekode.tp2intervals.integration.platform.trainerroad

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.domain.workout.structure.SingleStep
import org.freekode.tp2intervals.integration.platform.trainerroad.activity.TrainerRoadActivityMapper
import org.freekode.tp2intervals.integration.platform.trainerroad.activity.dto.TrainerRoadActivityDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.configuration.TrainerRoadConfigurationRepository
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.TRWorkoutMapper
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate


@CacheConfig(cacheNames = ["trWorkoutCache"])
@Service
class TRApiClientService(
    private val trApiClient: TRApiClient,
    private val trainerRoadConfigurationRepository: TrainerRoadConfigurationRepository,
) {
     private val logger = AppLogger.get(this.javaClass)

    fun findWorkoutsFromLibraryByName(name: String): List<WorkoutDetails> {
        val removeHtmlTags = trainerRoadConfigurationRepository.getConfiguration().removeHtmlTags
        return trApiClient.findWorkouts(name, 0, 500).workouts
            .map { TRWorkoutMapper().toWorkoutDetails(it, removeHtmlTags) }
    }

    fun getWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate, memberId: Long): List<Workout> {
        return trApiClient.getTimeline(memberId, startDate.toString(), endDate.toString())
            .plannedActivities
            .filter { it.date.toLocalDate() in startDate..endDate }
            .filter { it.workoutId != null }
            .map { plannedActivity ->
                getWorkout(plannedActivity.workoutId!!.toString())
                    .withDate(plannedActivity.date.toLocalDate().atStartOfDay())
            }
    }

    @Cacheable
    fun getWorkout(trWorkoutId: String): Workout {
        val removeHtmlTags = trainerRoadConfigurationRepository.getConfiguration().removeHtmlTags
        val trWorkoutMapper = TRWorkoutMapper()
        return trApiClient.getWorkout(trWorkoutId)
            .let { trWorkoutMapper.toWorkout(it, removeHtmlTags) }
            .also { workout ->
                logger.debugL3In(
                    "Mapped TrainerRoad workout {}, target preview: {}",
                    trWorkoutId,
                    targetPreview(workout),
                )
            }
    }

    fun getActivities(memberId: Long, startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val activityIds = trApiClient.getTimeline(memberId, startDate.toString(), endDate.toString())
            .activities
            .filter {
                val startedDate = it.started?.toLocalDate()
                startedDate != null && startedDate in startDate..endDate
            }
            .map { it.id }

        if (activityIds.isEmpty()) {
            return emptyList()
        }

        val activities = trApiClient.getActivities(memberId, activityIds.joinToString(","))
            .filter { it.date.toLocalDate() in startDate..endDate }
        val activityMapper = TrainerRoadActivityMapper()
        return activities.map { mapToActivity(activityMapper, it) }
    }

    private fun mapToActivity(activityMapper: TrainerRoadActivityMapper, it: TrainerRoadActivityDTO): Activity {
        val activityId = it.completedRide?.WorkoutRecordId ?: it.activityId
        val resource = trApiClient.exportFit(activityId.toString())
        return activityMapper.mapToActivity(it, resource)
    }

    private fun targetPreview(workout: Workout): String {
        return workout.structure?.steps
            ?.filterIsInstance<SingleStep>()
            ?.take(8)
            ?.joinToString { "${it.name}:${it.target.start}-${it.target.end}" }
            ?: "no structure"
    }
}