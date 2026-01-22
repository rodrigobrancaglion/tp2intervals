package org.freekode.tp2intervals.app.schedule

import org.freekode.tp2intervals.app.CopyC2CRequest
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
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