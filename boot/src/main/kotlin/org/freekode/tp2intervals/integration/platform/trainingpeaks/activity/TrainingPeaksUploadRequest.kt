package org.freekode.tp2intervals.integration.platform.trainingpeaks.activity

data class TrainingPeaksUploadRequest(
    val workoutDay: String,        // Formato "yyyy-MM-dd"
    val data: String,              // O conteúdo do .fit em Base64
    val fileName: String,
    val uploadClient: String = "TP Web App"
)