package org.freekode.tp2intervals.integration.platform.trainingpeaks.library.dto

import java.io.Serializable

data class TPWorkoutLibraryDTO(
    val exerciseLibraryId: String,
    val libraryName: String,
    val ownerName: String,
) : Serializable
