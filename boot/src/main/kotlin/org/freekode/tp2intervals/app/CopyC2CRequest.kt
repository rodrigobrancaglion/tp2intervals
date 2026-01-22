package org.freekode.tp2intervals.app

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import java.time.LocalDate

data class CopyC2CRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val types: List<BaseType>,
    val skipSynced: Boolean,
    val sourcePlatform: Platform,
    val targetPlatform: Platform
)
