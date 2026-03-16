package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IntervalsWorkoutRepository
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

@Repository
class IntervalsActivityRepository(
    private val intervalsActivityApiClient: IntervalsActivityApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
    private val intervalsWorkoutRepository: IntervalsWorkoutRepository
) : IActivityRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun platform() = Platform.INTERVALS

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId
        activities.forEach { activity ->
            CompletableFuture.runAsync {
                try {
                    if (activity.resource != null) {
                        // Upload .fit file to create a full activity in ICU
                        val fitBytes = Base64.getDecoder().decode(activity.resource)
                        val fileName = activity.fileName ?: "${activity.workoutId}.fit"
                        val multipart = ByteArrayMultipartFile(fitBytes, fileName)
                        val name = activity.title ?: "Activity ${activity.workoutId}"

                        log.info("Creating activity in ICU via .fit upload: workoutId=${activity.workoutId}, fileName=$fileName")
                        val response = intervalsActivityApiClient.createActivity(athleteId, name, multipart)

                        removeDuplicateWorkouts(response, activity, types)
                    } else {
                        // No .fit available: update RPE/FEEL only if the activity already exists in ICU
                        val newActivityDTO = IntervalsToActivityConverter().toDTO(activity, types)
                        intervalsActivityApiClient.updateActivity(activity.workoutId.toString(), newActivityDTO)
                    }
                } catch (e: Exception) {
                    log.error("Error saving activity ${activity.workoutId}: ${e.message}", e)
                }
            }
        }
    }

    fun removeDuplicateWorkouts(response: ActivityResponseDTO, activity: Activity, types: List<BaseType>){
        log.info("Activity created in ICU: workoutId=${response.id}, TP workoutId=${activity.workoutId}, title=${activity.title}")

        val startedAt = activity.startedAt?.toLocalDate() ?: LocalDate.now()
        val workouts = intervalsWorkoutRepository.getWorkouts(startedAt, startedAt)

        val originalWorkout = workouts.firstOrNull { event ->
            val externalData = ExternalData.empty().fromSimpleString(event.description ?: "")
            externalData.trainingPeaksId == activity.workoutId.toString()
        }

        val fitWorkout = workouts.firstOrNull { event ->
            val externalData = ExternalData.empty().fromSimpleString(event.description ?: "")
            externalData.trainingPeaksId == null && event.id != originalWorkout?.id
        }

        log.info("Original workout: workoutId=${originalWorkout?.id}, name=${originalWorkout?.name}")
        log.info("Fit workout (to delete): workoutId=${fitWorkout?.id}, name=${fitWorkout?.name}")

        if (originalWorkout != null) {
            val activityCompleted = Activity(originalWorkout.id, activity.rpe, activity.feel)
            val activityToUpdate = IntervalsToActivityConverter().toDTO(activityCompleted, types)
            intervalsActivityApiClient.updateActivity(response.id, activityToUpdate)
        }
        if (fitWorkout != null) {
            intervalsWorkoutRepository.deleteEvent(fitWorkout.id)
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
                IntervalsToActivityConverter().toDomain(it, pairedWorkout)
            }
    }
}
