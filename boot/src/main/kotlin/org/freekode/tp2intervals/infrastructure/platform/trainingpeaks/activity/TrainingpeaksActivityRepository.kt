package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.activity


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.activity.ActivityRepository
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.TrainingPeaksApiClient
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.TPToWorkoutConverter
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["tpActivitiesCache"])
@Repository
class TrainingpeaksActivityRepository(
    private val trainingPeaksApiClient: TrainingPeaksApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
    private val tpToWorkoutConverter: TPToWorkoutConverter,

    private val objectMapper: ObjectMapper
) : ActivityRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        objectMapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(false)

        val athleteId = trainingPeaksUserRepository.getUser().userId
        activities.forEach { activity ->
            val workoutDTO = trainingPeaksApiClient.getWorkout(athleteId, activity.workoutId)

            val newActivityDTO = tpToWorkoutConverter.convertToPutRequest(activity, workoutDTO, types)

            val jsonString = objectMapper.writeValueAsString(newActivityDTO)
            val finalJson = jsonString.replace(".0,", ",").replace(".0]", "]")

            trainingPeaksApiClient.updateActivity(athleteId, newActivityDTO.workoutId, finalJson)
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val userId = trainingPeaksUserRepository.getUser().userId
        val tpWorkouts = trainingPeaksApiClient.getWorkouts(userId, startDate.toString(), endDate.toString())

        val activities = tpWorkouts
            .filter { it.hasValidActitivy() }
            .map {
                tpToWorkoutConverter.toActivityDomain(it)
            }

        return activities
    }
}
