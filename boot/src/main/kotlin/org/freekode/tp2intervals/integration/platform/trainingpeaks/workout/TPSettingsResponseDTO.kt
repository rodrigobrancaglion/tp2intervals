package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TPSettingsResponseDTO(
    val athleteId: Long,
    val powerZones: List<TPZoneGroupDTO> = emptyList(),
    val heartRateZones: List<TPZoneGroupDTO> = emptyList(),
    val speedZones: List<TPZoneGroupDTO> = emptyList(),
    val firstName: String? = null,
    val lastName: String? = null,
    val timeZone: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TPZoneGroupDTO(
    val threshold: Double,
    val zones: List<TPZoneRangeDTO> = emptyList(),
    val workoutTypeId: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TPZoneRangeDTO(
    val label: String,
    val minimum: Double,
    val maximum: Double
)