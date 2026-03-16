package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.fit.FitFileReader
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPAttachmentService
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPToWorkoutConverter
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TrainingPeaksWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["tpActivitiesCache"])
@Repository
class TrainingpeaksActivityRepository(
    private val trainingPeaksWorkoutApiClient: TrainingPeaksWorkoutApiClient,
    private val trainingPeaksActivityApiClient: TrainingPeaksActivityApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
    private val tpToWorkoutConverter: TPToWorkoutConverter,
    private val tpAttachmentService: TPAttachmentService,

    objectMapper: ObjectMapper

) : IActivityRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    private val tpMapper = objectMapper.copy().apply {
        nodeFactory = JsonNodeFactory(false)
    }

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        val athleteId = trainingPeaksUserRepository.getUser().userId
        activities.forEach { activity ->
            val workoutDTO = trainingPeaksWorkoutApiClient.getWorkout(athleteId, activity.workoutId)

            val newActivityDTO = tpToWorkoutConverter.convertToPutRequest(activity, workoutDTO, types)

            val jsonString = tpMapper.writeValueAsString(newActivityDTO)
            val finalJson = jsonString.replace(".0,", ",").replace(".0]", "]")

            trainingPeaksActivityApiClient.updateActivity(athleteId, newActivityDTO.workoutId, finalJson)
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val userId = trainingPeaksUserRepository.getUser().userId
        val tpWorkouts = trainingPeaksWorkoutApiClient.getWorkouts(userId, startDate.toString(), endDate.toString())

        val activities = tpWorkouts
            .filter { it.hasValidActitivy() }
            .map { workout ->
                val activity = tpToWorkoutConverter.toActivityDomain(workout)

                // Try to populate the FIT file resource for upload to ICU
                val attachments = tpAttachmentService.getAttachments(userId, workout.workoutId)
                val attachment = attachments.firstOrNull()

                if (attachment != null) {
                    // Decode the Base64 FIT content and read device product name
                    val fitBytes = java.util.Base64.getDecoder().decode(attachment.content)
                    val isGzipped = attachment.name.endsWith(".gz")
                    val deviceProductName = FitFileReader.readProductName(fitBytes, isGzipped)

                    activity.copy(resource = attachment.content, fileName = attachment.name, deviceProductName = deviceProductName)
                } else {
                    activity
                }
            }

        return activities
    }
}
