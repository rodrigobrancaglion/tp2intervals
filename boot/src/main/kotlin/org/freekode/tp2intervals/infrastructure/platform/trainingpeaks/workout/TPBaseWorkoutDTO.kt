package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.freekode.tp2intervals.domain.CategoryType
import org.freekode.tp2intervals.domain.TrainingType
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.ALWAYS)
abstract class TPBaseWorkoutDTO<T>(
    val workoutId: Long,
    val athleteId: Long?,
    val workoutTypeValueId: Int,
    val workoutType: String?,
    val title: String?,
    val description: String?,
    val coachComments: String?,
    val workoutDay: LocalDateTime,
    val startTime: String?,
    val startTimePlanned: String?,
    val lastModifiedDate: String?,

    // Metrics - Planned
    val totalTimePlanned: Double?,
    val tssPlanned: Int?,
    val ifPlanned: Double?,
    val distancePlanned: Double?,
    val caloriesPlanned: Double?,
    val energyPlanned: Double?,
    val elevationGainPlanned: Double?,
    val velocityPlanned: Double?,

    // Metrics - Actual
    val totalTime: Double?,
    val distance: Double?,
    val calories: Int?,
    val energy: Double?,
    var rpe: Int?,
    var feeling: Int?,
    @JsonProperty("if")
    val `if`: Double?,
    val tssSource: Int?,
    val tssActual: Int?,
    val normalizedPowerActual: Double?,
    val powerAverage: Double?,
    val powerMaximum: Double?,
    val torqueAverage: Double?,
    val torqueMaximum: Double?,
    val heartRateAverage: Int?,
    val heartRateMaximum: Int?,
    val heartRateMinimum: Int?,
    val cadenceAverage: Int?,
    val cadenceMaximum: Int?,
    val elevationGain: Double?,
    val elevationLoss: Double?,
    val elevationAverage: Double?,
    val elevationMaximum: Double?,
    val elevationMinimum: Double?,
    val velocityAverage: Double?,
    val velocityMaximum: Double?,
    val normalizedSpeedActual: Double?,

    // Compliance & Meta
    val complianceTssPercent: Double?,
    val complianceDurationPercent: Double?,
    val complianceDistancePercent: Double?,
    val personalRecordCount: Int?,
    val orderOnDay: Int?,
    val userTags: String?,
    val isItAnOr: Boolean?,
    val completed: Boolean?,
    val isLocked: Boolean?,
    val isHidden: Boolean?,
    val publicSettingValue: Int?,
    val sharedWorkoutInformationKey: String?,
    val sharedWorkoutInformationExpireKey: String?,
    val workoutDeviceSource: String?,
    val hasPrivateWorkoutNoteForCaller: Boolean?,
    val code: String?,

    // IDs and Equipment
    val workoutSubTypeId: Int?,
    val equipmentBikeId: Long?,
    val equipmentShoeId: Long?,
    val poolLengthOptionId: Int?,

    // Complex Arrays and Objects
    open var structure: T?,
    val workoutComments: List<TPWorkoutCommentDTO> = emptyList(),

    // Temperatures
    val tempMax: Double?,
    val tempMin: Double?,
    val tempAvg: Double?,

    val syncedTo: List<String>? = emptyList(),
    val newComment: String? = null,

    // Customizations
    val distanceCustomized: Double?,
    val distanceUnitsCustomized: Int?
) {
    fun getWorkoutType(): TrainingType? = workoutTypeValueId.let { TPTrainingTypeMapper.getByValue(it) }

    fun hasValidActitivy(): Boolean = tssSource != null && tssSource != 0 && `if` != null

    /**
    * Determines if a workout is valid for processing.
    * A workout is valid if it has all planned metrics (Time, TSS, IF)
    * OR if its type category is not WORKOUT (e.g., Notes, Day-off, Bricks).
    */
    fun hasValidWorkout(): Boolean {
        // Check if the primary metrics are present
        return totalTimePlanned != null && tssPlanned != null && ifPlanned != null
    }

    fun isNotWorkout(): Boolean {
        // Map the integer ID to the domain TrainingType enum
        val trainingType = TPTrainingTypeMapper.getByValue(workoutTypeValueId)

        // Check if the type belongs to a non-workout category (e.g., CategoryType.NOTE)
        val isNotAWorkoutCategory = trainingType.category != CategoryType.WORKOUT

        return isNotAWorkoutCategory
    }

    fun mapType(): TrainingType = workoutTypeValueId.let { TPTrainingTypeMapper.getByValue(it) }
}
