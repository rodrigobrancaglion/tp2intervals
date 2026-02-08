package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.WellnessType

class TPMetricsTypeMapper {
    companion object {
        private val typeMap = mapOf(
            //METRICS
            WellnessType.WEIGHT to 9
        )

        fun getByValue(value: Int): WellnessType =
            typeMap.filterValues { it == value }.keys.firstOrNull() ?: WellnessType.WEIGHT

        fun getByType(metricType: WellnessType): Int = typeMap[metricType]!!

    }
}
