package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TPUserRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPAttachmentService
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPToWorkoutConverter
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPWorkoutApiClient
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.freekode.tp2intervals.integration.utils.FitFileReader
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["tpActivitiesCache"])
@Repository
class TPActivityRepository(
    private val TPWorkoutApiClient: TPWorkoutApiClient,
    private val TPActivityApiClient: TPActivityApiClient,
    private val TPUserRepository: TPUserRepository,
    private val tpToWorkoutConverter: TPToWorkoutConverter,
    private val tpAttachmentService: TPAttachmentService,

    objectMapper: ObjectMapper

) : IActivityRepository {
    override fun platform() = Platform.TRAINING_PEAKS

     private val logger = AppLogger.get(this.javaClass)

    private val tpMapper = objectMapper.copy().apply {
        nodeFactory = JsonNodeFactory(false)
    }

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        val athleteId = TPUserRepository.getUser().userId
        activities.forEach { activity ->
            try {
                if (activity.resource != null) {
                    // Rouvy (or other source) has a .fit file — upload it directly to TP
                    uploadDirectFit(athleteId, activity)
                } else {
                    // No .fit: update RPE/Feel on an existing TP workout
                    val workoutDTO = TPWorkoutApiClient.getWorkout(athleteId, activity.workoutId)
                    val newActivityDTO = tpToWorkoutConverter.convertToPutRequest(activity, workoutDTO, types)
                    val jsonString = tpMapper.writeValueAsString(newActivityDTO)
                    val finalJson = jsonString.replace(".0,", ",").replace(".0]", "]")
                    TPActivityApiClient.updateActivity(athleteId, newActivityDTO.workoutId, finalJson)
                }
            } catch (e: Exception) {
                logger.errorL3In("TrainingPeaks - Error saving activity ${activity.workoutId} : ${e.message}", e)
            }
        }
    }

    private fun uploadDirectFit(userId: String, activity: Activity) {
        val fileName = activity.fileName ?: "activity_${activity.workoutId}.fit"

        // 1. Format date as yyyy-MM-dd
        val workoutDay = activity.startedAt?.toString() ?: LocalDate.now().toString()

        // 2. Build upload request object
        val uploadRequest = TPUploadRequest(
            workoutDay = workoutDay,
            data = activity.resource.toString(),
            fileName = fileName,
            uploadClient = "TP Web App"
        )

        logger.infoL3In("Uploading JSON Base64 to TrainingPeaks: fileName=$fileName")

        try {
            TPActivityApiClient.uploadActivity(userId, uploadRequest)
            logger.infoL3In("TrainingPeaks - Successfully uploaded activity")
        } catch (e: Exception) {
            if (e.message?.contains("File has already been uploaded") == true) {
                logger.infoL3In("TrainingPeaks - Activity already uploaded: $fileName")
            } else {
                logger.errorL3In("TrainingPeaks - Error uploading: ${e.message}", e)
            }
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val userId = TPUserRepository.getUser().userId
        val tpWorkouts = TPWorkoutApiClient.getWorkouts(userId, startDate.toString(), endDate.toString())

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
