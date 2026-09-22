package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

data class TPUploadRequest(
    val workoutDay: String,        // Format "yyyy-MM-dd"
    val data: String,              // Base64 encoded .fit content
    val fileName: String,
    val uploadClient: String = "TP Web App"
)