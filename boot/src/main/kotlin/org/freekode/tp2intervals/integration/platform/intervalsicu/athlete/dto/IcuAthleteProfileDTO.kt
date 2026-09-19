package org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Main wrapper for the Intervals.icu athlete profile response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class IntervalsAthleteProfileDTO(
    /** Internal Intervals.icu athlete ID (e.g., "i310762") */
    val id: String,
    val name: String?,
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val sex: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val timezone: String?,

    /** Profile picture URL */
    @JsonProperty("profile_medium")
    val profileMedium: String?,

    /** List of bike equipment (IDs start with 'b') */
    val bikes: List<EquipmentDTO> = emptyList(),

    /** List of shoe equipment (IDs start with 'g') */
    val shoes: List<EquipmentDTO> = emptyList(),

    /** Athlete's weight in kilograms */
    @JsonProperty("icu_weight")
    val icuWeight: Double?,

    /** Resting Heart Rate used for calculations */
    @JsonProperty("icu_resting_hr")
    val icuRestingHr: Int?,

    /** Specific settings per sport (Power, HR, and Pace zones) */
    val sportSettings: List<SportSettingsDTO> = emptyList(),
) {
    /**
     * Member function: Integrated directly into the DTO structure.
     */
    fun findRideSettings(): SportSettingsDTO? {
        return sportSettings.find { it.types.contains("Ride") }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class EquipmentDTO(
    val id: String,
    val name: String,
    /** Total distance covered by this equipment in meters */
    val distance: Double?,
    /** Flag indicating if this is the default equipment for its category */
    val primary: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SportSettingsDTO(
    val id: Int,

    /** Sport types covered by this setting (e.g., ["Ride", "VirtualRide"]) */
    val types: List<String> = emptyList(),

    val ftp: Int?,
    val lthr: Int?,

    @JsonProperty("max_hr")
    val maxHr: Int?,

    @JsonProperty("threshold_pace")
    val thresholdPace: Double?,

    @JsonProperty("pace_units")
    val paceUnits: String?,

    /** * FIXED: Made nullable to avoid MissingKotlinParameterException.
     * Some sports (like swimming or walking) might not have power zones.
     */
    @JsonProperty("power_zones")
    val powerZones: List<Int>? = emptyList(),

    @JsonProperty("power_zone_names")
    val powerZoneNames: List<String>? = emptyList(),

    /** Heart rate zone upper limits - Also made nullable for safety */
    @JsonProperty("hr_zones")
    val hrZones: List<Int>? = emptyList(),

    @JsonProperty("hr_zone_names")
    val hrZoneNames: List<String>? = emptyList(),

    /** Pace zone upper limits */
    @JsonProperty("pace_zones")
    val paceZones: List<Double>? = null,

    @JsonProperty("pace_zone_names")
    val paceZoneNames: List<String>? = emptyList(),

    @JsonProperty("mmp_model")
    val mmpModel: MMPModelDTO?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MMPModelDTO(
    /** Model type (e.g., "MORTON_3P") */
    val type: String?,
    /** Estimated Critical Power */
    val criticalPower: Int?,
    /** W-Prime (Anaerobic work capacity in Joules) */
    val wPrime: Int?,
    /** Maximum Power (Peak) */
    val pMax: Int?
)