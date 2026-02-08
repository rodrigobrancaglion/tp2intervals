package org.freekode.tp2intervals.integration.platform.intervalsicu.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class IntervalsSportSettingsDTO(
    val id: Long,
    @JsonProperty("athlete_id")
    val athleteId: String,
    val types: List<String> = emptyList(),

    // Performance Thresholds
    val ftp: Int?,
    @JsonProperty("indoor_ftp")
    val indoorFtp: Int?,
    val lthr: Int?,
    @JsonProperty("max_hr")
    val maxHr: Int?,
    @JsonProperty("threshold_pace")
    val thresholdPace: Double?,

    // Power Zones
    @JsonProperty("power_zones")
    val powerZones: List<Int> = emptyList(),
    @JsonProperty("power_zone_names")
    val powerZoneNames: List<String> = emptyList(),

    // Heart Rate Zones
    @JsonProperty("hr_zones")
    val hrZones: List<Int> = emptyList(),
    @JsonProperty("hr_zone_names")
    val hrZoneNames: List<String> = emptyList(),

    // Pace Zones
    @JsonProperty("pace_zones")
    val paceZones: List<Double>? = null,
    @JsonProperty("pace_zone_names")
    val paceZoneNames: List<String>? = null,

    // Settings & Config
    @JsonProperty("warmup_time")
    val warmupTime: Int?,
    @JsonProperty("cooldown_time")
    val cooldownTime: Int?,
    @JsonProperty("hr_load_type")
    val hrLoadType: String?,
    @JsonProperty("pace_units")
    val paceUnits: String?,
    @JsonProperty("default_gear_id")
    val defaultGearId: String?,

    // Display and Charts
    val display: IntervalsDisplayDTO?,
    @JsonProperty("activity_charts")
    val activityCharts: Map<String, Any?>?,

    // Metadata
    val created: String?,
    val updated: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class IntervalsDisplayDTO(
    val color: String?,
    val color2: String?,
    val colorScheme: String?,
    val lowIntensity: Int?,
    val highIntensity: Int?,
    val showName: Boolean = true,
    val shrinkWarmup: Boolean = true,
    val shrinkCooldown: Boolean = true
)