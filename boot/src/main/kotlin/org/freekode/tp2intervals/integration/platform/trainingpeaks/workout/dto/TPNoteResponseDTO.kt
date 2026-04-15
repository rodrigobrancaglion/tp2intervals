package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto

import java.time.LocalDateTime

data class TPNoteResponseDTO(
    var id: Long,
    var noteDate: LocalDateTime,
    var title: String,
    var description: String?,
)
