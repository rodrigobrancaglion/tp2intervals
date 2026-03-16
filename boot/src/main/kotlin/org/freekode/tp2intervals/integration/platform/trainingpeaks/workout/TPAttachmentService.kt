package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.workout.Attachment
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TPAttachmentService(
    private val trainingPeaksWorkoutApiClient: TrainingPeaksWorkoutApiClient,
    @param:Value("\${app.attachments.enabled}") private val attachmentsEnabled: Boolean,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun getAttachments(userId: String, workoutId: Long): List<Attachment> {
        if (!attachmentsEnabled) {
            log.info("Attachments not enabled, skipping FIT file download for workoutId=$workoutId")
            return listOf()
        }

        val workoutDetails = trainingPeaksWorkoutApiClient.getWorkoutDetails(userId, workoutId)
        val fileId = workoutDetails.getFirstFitFileId()
        val fileName = workoutDetails.getFirstFitFileName()

        if (fileId == null || fileName == null) {
            log.info("No FIT file found for workoutId=$workoutId")
            return listOf()
        }

        log.info("Downloading FIT file for workoutId=$workoutId, fileId=$fileId, fileName=$fileName")
        val resource = trainingPeaksWorkoutApiClient.downloadWorkoutAttachment(userId, workoutId, fileId)
        return listOf(Attachment(fileName, resource))
    }

}
