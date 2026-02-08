package org.freekode.tp2intervals.dto.schedule

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.CopyC2CRequest
import java.time.LocalDate

data class C2CTodayScheduledRequest(
    val types: List<BaseType>,
    val skipSynced: Boolean,
    val sourcePlatform: Platform,
    val targetPlatform: Platform
) : Schedulable {
    fun forToday() = CopyC2CRequest(
        LocalDate.now(),
        LocalDate.now(),
        types,
        skipSynced,
        sourcePlatform,
        targetPlatform
    )

    inline fun <reified T : Enum<T>> hasType(): Boolean {
        return this.types.any { it is T }
    }

}