package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

class TPWorkoutDetailsResponseDTO(
    val workoutId: String,
    val attachmentFileInfos: List<FileInfoDTO>?,
    val workoutDeviceFileInfos: List<DeviceFileInfoDTO>?,
) {
    /**
     * Attachment files manually added to the workout (e.g. imported files).
     */
    class FileInfoDTO(
        val fileId: String,
        val fileName: String,
        val fileSystemId: String?,
        val dateUploaded: String?,
    )

    /**
     * Device-generated FIT files automatically associated with the workout after sync.
     */
    class DeviceFileInfoDTO(
        val fileId: Long,
        val fileSystemId: String,
        val fileName: String,
        val dateUploaded: String?,
    )

    /**
     * Returns the first available FIT file ID — prefers device files over manual attachments.
     */
    fun getFirstFitFileId(): String? {
        val deviceFile = workoutDeviceFileInfos?.firstOrNull()
        if (deviceFile != null) return deviceFile.fileId.toString()
        return attachmentFileInfos?.firstOrNull()?.fileId
    }

    /**
     * Returns the first available FIT file name.
     */
    fun getFirstFitFileName(): String? {
        return workoutDeviceFileInfos?.firstOrNull()?.fileName
            ?: attachmentFileInfos?.firstOrNull()?.fileName
    }
}
