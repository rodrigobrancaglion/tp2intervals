package org.freekode.tp2intervals.integration.platform.strava.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.ByteArrayMultipartFile
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

/**
 * Repository that handles uploading executed activities from TP to Strava.
 * Follows the same IActivityRepository pattern as IntervalsActivityRepository.
 */
@Repository
class StravaActivityRepository(
    private val stravaActivityUploadClient: StravaActivityUploadClient,
) : IActivityRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val maxPollAttempts = 10
    private val pollIntervalMs = 2000L

    override fun platform() = Platform.STRAVA

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        activities.forEach { activity ->
            try {
                if (activity.resource == null) {
                    log.info("No FIT file for activity workoutId=${activity.workoutId}, skipping Strava upload")
                    return@forEach
                }
                uploadToStrava(activity)
            } catch (e: Exception) {
                log.error("Error uploading activity workoutId=${activity.workoutId} to Strava", e)
            }
        }
    }

    /**
     * Uploads the FIT file to Strava and polls for completion.
     * If the activity title starts with "ROUVY", uses the title as description.
     */
    private fun uploadToStrava(activity: Activity) {
        val fitBytes = Base64.getDecoder().decode(activity.resource)
        val fileName = activity.fileName ?: "${activity.workoutId}.fit.gz"
        val originalTitle = activity.title ?: "Activity ${activity.workoutId}"
        val dataType = if (fileName.endsWith(".gz")) "fit.gz" else "fit"
        val multipart = ByteArrayMultipartFile(fitBytes, fileName)

        // Use title as name; Rouvy route enrichment is commented out pending Rouvy public API
        val finalName = "ROUVY | $originalTitle"
//        val finalName = getRouteName(fileName) ?: originalTitle

        log.info("Uploading to Strava: workoutId=${activity.workoutId}, fileName=$fileName, dataType=$dataType, device=${activity.deviceProductName}, description=$finalName")

        val uploadResponse = stravaActivityUploadClient.createUpload(
            file = multipart,
            dataType = dataType,
            name = finalName,
            description = originalTitle,
            externalId = activity.workoutId.toString(),
        )

        log.info("Strava upload queued: uploadId=${uploadResponse.id}, status=${uploadResponse.status}")

        // Poll until Strava finishes processing the upload
        pollUntilComplete(uploadResponse.id, activity.workoutId)
    }

    /**
     * Polls Strava until the upload is processed or max attempts reached.
     * Handles duplicate detection gracefully — skips instead of logging as error.
     */
    private fun pollUntilComplete(uploadId: Long, workoutId: Long) {
        repeat(maxPollAttempts) { attempt ->
            Thread.sleep(pollIntervalMs)
            val status = stravaActivityUploadClient.getUploadStatus(uploadId)
            log.info("Strava upload poll attempt=${attempt + 1}: status=${status.status}, activity_id=${status.activity_id}")

            if (!status.error.isNullOrBlank()) {
                if (status.error.contains("duplicate", ignoreCase = true)) {
                    log.info("Activity workoutId=$workoutId already exists on Strava (duplicate), skipping")
                } else {
                    log.error("Strava upload error for uploadId=$uploadId: ${status.error}")
                }
                return
            }
            if (status.activity_id != null) {
                log.info("Strava activity created successfully: activity_id=${status.activity_id}")
                return
            }
        }
        log.warn("Strava upload polling timed out for uploadId=$uploadId after $maxPollAttempts attempts")
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        // Strava is write-only in this context — activities come from TP
        return emptyList()
    }

    private fun getRouteName(fileName: String): String?{
        return null
        // Extrai o ID: remove .gz primeiro, depois remove .fit
        // Ex: "260319164706-96942331.fit.gz" -> "260319164706-96942331"
//        if (fileName.contains("-")) {
//            val routeName = rouvyMetadataService.getRouteName(fileName)
//            if (!routeName.isNullOrBlank()) {
//                return routeName // Ex: "Challenge Sanremo | Italy"
//            }
//        }
    }
}
