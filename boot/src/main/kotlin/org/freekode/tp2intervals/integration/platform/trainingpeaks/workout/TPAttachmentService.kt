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
            log.info("Attachments not enabled")
            return emptyList()
        }

        val workoutDetails = trainingPeaksWorkoutApiClient.getWorkoutDetails(userId, workoutId)
        return workoutDetails.attachmentFileInfos
            .map {
                val attachment = trainingPeaksWorkoutApiClient.downloadWorkoutAttachment(userId, workoutId, it.fileId)
                Attachment(it.fileName, attachment)
            }
    }
}
