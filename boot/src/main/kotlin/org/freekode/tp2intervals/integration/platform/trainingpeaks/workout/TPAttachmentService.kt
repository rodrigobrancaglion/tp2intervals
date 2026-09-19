package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.workout.Attachment
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TPAttachmentService(
    private val trainingPeaksWorkoutApiClient: TrainingPeaksWorkoutApiClient,
    @param:Value("\${app.attachments.enabled}") private val attachmentsEnabled: Boolean,
) {
     private val logger = AppLogger.get(this.javaClass)

    fun getAttachments(userId: String, workoutId: Long): List<Attachment> {
        if (!attachmentsEnabled) {
            logger.infoL3In("Attachments not enabled, skipping FIT file download for workoutId: {$workoutId}")
            return listOf()
        }

        val workoutDetails = trainingPeaksWorkoutApiClient.getWorkoutDetails(userId, workoutId)
        val fileId = workoutDetails.firstFitFileId
        val fileName = workoutDetails.firstFitFileName

        if (fileId == null || fileName == null) {
            logger.warnL3In("No FIT file found for workoutId: {$workoutId}")
            return listOf()
        }

        logger.infoL3In("Downloading FIT file for workoutId: {$workoutId}, fileId: {$fileId}, fileName: {$fileName}")
        val resource = trainingPeaksWorkoutApiClient.downloadWorkoutAttachment(userId, workoutId, fileId)
        return listOf(Attachment(fileName, resource))
    }

}
