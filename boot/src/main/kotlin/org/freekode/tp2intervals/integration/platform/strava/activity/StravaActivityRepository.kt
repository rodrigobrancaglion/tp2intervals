package org.freekode.tp2intervals.integration.platform.strava.activity

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.ByteArrayMultipartFile
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
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

     private val logger = AppLogger.get(this.javaClass)
    private val maxPollAttempts = 10
    private val pollIntervalMs = 2000L

    override fun platform() = Platform.STRAVA

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        activities.forEach { activity ->
            try {
                if (activity.resource == null) {
                    logger.infoL3In("No FIT file for activity workoutId=${activity.workoutId}, skipping Strava upload")
                    return@forEach
                }
                uploadToStrava(activity)
            } catch (e: Exception) {
                logger.errorL3In("Strava - Error uploading activity ${activity.workoutId} : ${e.message}", e)
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

        logger.infoL3In("Uploading: workoutId=${activity.workoutId}, fileName=$fileName, dataType=$dataType, device=${activity.deviceProductName}, description=$originalTitle")

        val uploadResponse = stravaActivityUploadClient.createUpload(
            file = multipart,
            dataType = dataType,
            name = originalTitle,
            description = activity.description,
            externalId = activity.workoutId.toString(),
        )

        logger.infoL3In("Upload queued: uploadId=${uploadResponse.id}, status=${uploadResponse.status}")

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
            logger.infoL3In("Upload poll attempt=${attempt + 1}: status=${status.status}, activity_id=${status.activity_id}")

            if (!status.error.isNullOrBlank()) {
                if (status.error.contains("duplicate", ignoreCase = true)) {
                    logger.infoL3In("Activity workoutId=$workoutId already exists on Strava (duplicate), skipping")
                } else {
                    logger.errorL3In("Upload error for uploadId=$uploadId: ${status.error}")
                }
                return
            }
            if (status.activity_id != null) {
                logger.infoL3In("Activity created successfully: activity_id=${status.activity_id}")
                return
            }
        }
        logger.warnL3In("Upload polling timed out for uploadId=$uploadId after $maxPollAttempts attempts")
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        // Strava is write-only in this context — activities come from TP
        return emptyList()
    }

    fun uploadDirectFit(fitBytes: ByteArray, name: String) {
        val fileName = "${name.replace(" ", "_")}.fit"
        val multipart = ByteArrayMultipartFile(fitBytes, fileName)
        val finalName = if (name.startsWith("ROUVY")) name else "ROUVY | $name"

        logger.infoL3In("Uploading direct FIT to Strava: name=$finalName")

        val uploadResponse = stravaActivityUploadClient.createUpload(
            file = multipart,
            dataType = "fit",
            name = finalName,
            description = "Synced from Rouvy via Antigravity Automation",
            externalId = null,
        )

        logger.infoL3In("Direct upload queued: uploadId=${uploadResponse.id}, status=${uploadResponse.status}")
        // We don't necessarily need to poll here for automated sync, just fire and forget if it queued
    }
}
