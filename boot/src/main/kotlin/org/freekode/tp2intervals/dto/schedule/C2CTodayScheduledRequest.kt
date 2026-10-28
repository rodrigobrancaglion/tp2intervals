package org.freekode.tp2intervals.dto.schedule

import org.freekode.tp2intervals.domain.*
import org.freekode.tp2intervals.dto.CopyC2CRequest
import java.time.LocalDate

data class C2CTodayScheduledRequest(
    val types: List<BaseType>,
    val skipSynced: Boolean,
    val sourcePlatform: Platform,
    val targetPlatform: Platform? = null,
    val targetPlatforms: List<Platform> = emptyList()
) : Schedulable {
    fun forToday() = CopyC2CRequest(
        LocalDate.now(),
        LocalDate.now(),
        types,
        skipSynced,
        sourcePlatform,
        targetPlatform,
        if (targetPlatforms.isNotEmpty()) targetPlatforms else listOfNotNull(targetPlatform)
    )

    inline fun <reified T : Enum<T>> hasType(): Boolean {
        return when (T::class) {
            OtherType::class -> {
                this.types.any { it is OtherType || it == TrainingType.RACE }
            }
            TrainingType::class -> {
                this.types.any { it is TrainingType && it != TrainingType.RACE }
            }
            ActivityType::class -> {
                this.types.any { it is ActivityType } && this.types.none { it == TrainingType.RACE }
            }
            else -> this.types.any { it is T }
        }
    }

}