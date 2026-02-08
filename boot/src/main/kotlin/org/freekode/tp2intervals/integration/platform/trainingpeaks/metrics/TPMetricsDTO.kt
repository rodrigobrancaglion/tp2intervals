package org.freekode.tp2intervals.integration.platform.trainingpeaks.metrics

import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPMetricsTypeMapper

data class TPMetricsDTO(
    val id: String?,
    val athleteId: Long?,
    val timeStamp: String?, // Format: 2026-01-09T00:00:00
    val details: List<TPMetricsDetailDTO> = emptyList()
) {
    class TPMetricsDetailDTO(
        val parentId: Long?,
        val type: Int?,
        var value: Double?,
        val isPotentiallyNegative: Boolean?,
        val uploadClient: String?,
        val label: String?,
        val time: String?,
        val modifiedTime: String?
    )

    fun getMetricWeight(): Double? {
        return details.find { it.type == TPMetricsTypeMapper.getByType(WellnessType.WEIGHT) }?.value
    }

    fun getDetailByType(metricType: WellnessType): TPMetricsDetailDTO? {
        val tpType = TPMetricsTypeMapper.getByType(metricType)
        return details.find { it.type == tpType }
    }
}