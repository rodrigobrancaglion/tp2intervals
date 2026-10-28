package org.freekode.tp2intervals.dto

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import java.time.LocalDate

data class CopyC2CRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val types: List<BaseType>,
    val skipSynced: Boolean,
    val sourcePlatform: Platform,
    val targetPlatform: Platform? = null,
    val targetPlatforms: List<Platform> = emptyList()
) {
    fun allTargetPlatforms(): List<Platform> {
        return if (targetPlatforms.isNotEmpty()) {
            targetPlatforms
        } else if (targetPlatform != null) {
            listOf(targetPlatform)
        } else {
            emptyList()
        }
    }
}

