package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.FeelingType

/**
 * Mapper to translate Feeling metrics between TrainingPeaks (1,3,5,7,9 scale)
 * and Intervals.icu (1,2,3,4,5 scale).
 */
class TPTrainingFeelingMapper {
    companion object {

        /**
         * Centralized mapping: FeelingType to Pair(IntervalsValue, TrainingPeaksValue)
         * Intervals: 1 (Strong) to 5 (Weak)
         * TrainingPeaks: 1 (Strong) to 9 (Weak) - only odd numbers.
         */
        private val scaleMap = mapOf(
            FeelingType.STRONG to (1 to 1),
            FeelingType.GOOD   to (2 to 3),
            FeelingType.NORMAL to (3 to 5),
            FeelingType.POOR   to (4 to 7),
            FeelingType.WEAK   to (5 to 9)
        )

        /**
         * Maps Intervals (ICU) numeric value (1-5) to Domain FeelingType.
         */
        fun getByICUValue(value: Int?): FeelingType {
            return scaleMap.entries.find { it.value.first == value }?.key ?: FeelingType.NORMAL
        }

        /**
         * Returns the integer value expected by TrainingPeaks (1,3,5,7,9).
         */
        fun getTPValue(feelingType: FeelingType): Int {
            return scaleMap[feelingType]?.second ?: 5 // Default to TP Normal
        }

        /**
         * Maps TrainingPeaks (TP) numeric value (1,3,5,7,9) to Domain FeelingType.
         */
        fun getByTPValue(value: Int?): FeelingType {
            return scaleMap.entries.find { it.value.second == value }?.key ?: FeelingType.NORMAL
        }

        /**
         * Returns the integer value expected by Intervals.icu (1,2,3,4,5).
         */
        fun getICUValue(feelingType: FeelingType): Int {
            return scaleMap[feelingType]?.first ?: 3 // Default to ICU Normal
        }
    }
}