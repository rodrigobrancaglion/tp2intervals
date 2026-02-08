package org.freekode.tp2intervals.integration.platform.trainingpeaks.settings

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TrainingPeaksWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["tpSettingsCache"])
@Repository
class TrainingpeaksSettingsRepository(
    private val trainingPeaksWorkoutApiClient: TrainingPeaksWorkoutApiClient,
    private val trainingPeaksSettingsApiClient: TrainingPeaksSettingsApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,

    objectMapper: ObjectMapper

) : ISettingsRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    private val tpMapper = objectMapper.copy().apply {
        nodeFactory = JsonNodeFactory(false)
    }

    override fun save(activities: List<Activity>, types: List<BaseType>) {
        val userId = trainingPeaksUserRepository.getUser().userId
        val settings = trainingPeaksSettingsApiClient.getSettings(userId)

//        activities.forEach { activity ->
//            val workoutDTO = trainingPeaksApiClient.getWorkout(athleteId, activity.workoutId)
//
//            val newActivityDTO = tpToWorkoutConverter.convertToPutRequest(activity, workoutDTO, types)
//
//            val jsonString = tpMapper.writeValueAsString(newActivityDTO)
//            val finalJson = jsonString.replace(".0,", ",").replace(".0]", "]")
//
//            trainingPeaksApiClient.updateActivity(athleteId, newActivityDTO.workoutId, finalJson)
//        }
    }

    override fun get(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val userId = trainingPeaksUserRepository.getUser().userId
        val settings = trainingPeaksSettingsApiClient.getSettings(userId)

//        val tpWorkouts = trainingPeaksApiClient.getWorkouts(userId, startDate.toString(), endDate.toString())
//
//        val activities = tpWorkouts
//            .filter { it.hasValidActitivy() }
//            .map {
//                tpToWorkoutConverter.toActivityDomain(it)
//            }
//
//        return activities
        return emptyList<Activity>()
    }
}
