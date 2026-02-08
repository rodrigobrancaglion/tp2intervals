package org.freekode.tp2intervals.integration.platform.trainingpeaks.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TPSettingsDTO(
    val athleteId: Long,
    val personId: Long,
    val userName: String?,
    val firstName: String?,
    val lastName: String?,
    val fullName: String?,
    val email: String?,
    val country: String?,
    val timeZone: String?,
    val athleteType: Int,
    val coachedBy: Long?,

    // Zones
    val heartRateZones: List<HeartRateZoneGroup> = emptyList(),
    val powerZones: List<PowerZoneGroup> = emptyList(),
    val speedZones: List<SpeedZoneGroup> = emptyList(),

    // Notifications & Settings
    val thresholdsAutoApply: Boolean,
    val thresholdsNotifyAthlete: Boolean,
    val thresholdsNotifyCoach: Boolean,
    val units: Int,

    // Dates (Using String to avoid Jackson LocalDateTime parsing issues without extra modules)
    val created: String?,
    val lastLogon: String?,
    val birthday: String?,

    val nutritionSettings: NutritionSettings?,
    val iCalendarKeys: ICalendarKeys?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HeartRateZoneGroup(
    val threshold: Int,
    val maximumHeartRate: Int,
    val restingHeartRate: Int,
    val calculationMethod: Int,
    val workoutTypeId: Int,
    val zones: List<ZoneDetail> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PowerZoneGroup(
    val threshold: Int,
    val calculationMethod: Int,
    val workoutTypeId: Int,
    val zones: List<ZoneDetail> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpeedZoneGroup(
    val threshold: Double,
    val calculationMethod: Int,
    val workoutTypeId: Int,
    val zones: List<ZoneDetail> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ZoneDetail(
    val label: String,
    val minimum: Double,
    val maximum: Double
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NutritionSettings(
    val athleteId: Long,
    val plannedCalories: Int,
    val substrateUtilizationCategory: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ICalendarKeys(
    val workoutsAndMeals: String?,
    val workoutsOnly: String?,
    val mealsOnly: String?
)