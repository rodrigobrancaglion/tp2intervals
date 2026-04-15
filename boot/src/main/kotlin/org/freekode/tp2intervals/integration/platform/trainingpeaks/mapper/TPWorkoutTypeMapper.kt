package org.freekode.tp2intervals.integration.platform.trainingpeaks.mapper

import org.freekode.tp2intervals.domain.TrainingType

class TPWorkoutTypeMapper {
    companion object Companion {
        private val typeMap = mapOf(
            TrainingType.SWIM to 1,
            TrainingType.BIKE to 2,
            TrainingType.GRAVEL_BIKE to 2,
            TrainingType.RUN to 3,
            TrainingType.BRICK to 4,
            TrainingType.UNKNOWN to 5, // crosstrain
            TrainingType.RACE to 6, // ??
            TrainingType.DAY_OFF to 7,
            TrainingType.NOTE to 7,
            TrainingType.MTB to 8,
            TrainingType.STRENGTH to 9,
            TrainingType.WORKOUT to 9,
            TrainingType.UNKNOWN to 10, // custom
            TrainingType.UNKNOWN to 11, // xc-ski
            TrainingType.UNKNOWN to 12, // rowing
            TrainingType.UNKNOWN to 13, // walk
            TrainingType.UNKNOWN to 100, // other
        )

        fun getByValue(value: Int?): TrainingType =
            typeMap.filterValues { it == value }.keys.firstOrNull() ?: TrainingType.UNKNOWN

        fun getByType(trainingType: TrainingType): Int = typeMap[trainingType] ?: 100
    }
}