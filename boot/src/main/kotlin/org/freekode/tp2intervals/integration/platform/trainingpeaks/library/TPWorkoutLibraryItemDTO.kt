package org.freekode.tp2intervals.integration.platform.trainingpeaks.library

import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TPBaseWorkoutDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.structure.TPWorkoutStructureDTO
import java.time.LocalDateTime

class TPWorkoutLibraryItemDTO(
    exerciseLibraryItemId: Long,
    athleteId: Long?,
    workoutTypeId: Int,
    itemName: String,
    totalTimePlanned: Double?,
    tssPlanned: Int?,
    ifPlanned: Double?,
    ifSource: Double?,
    description: String?,
    coachComments: String?,
    rpe: Int?,
    feeling: Int?,
    structure: TPWorkoutStructureDTO?
) : TPBaseWorkoutDTO<TPWorkoutStructureDTO>(
    workoutId = exerciseLibraryItemId,
    athleteId = athleteId,
    workoutTypeValueId = workoutTypeId,
    workoutType = null, // Não disponível no LibraryItem
    title = itemName,
    description = description,
    coachComments = coachComments,
    workoutDay = LocalDateTime.now(),
    startTime = null,
    startTimePlanned = null,
    lastModifiedDate = null,

    // Metrics - Planned
    totalTimePlanned = totalTimePlanned,
    tssPlanned = tssPlanned,
    ifPlanned = ifPlanned,
    distancePlanned = null,
    caloriesPlanned = null,
    energyPlanned = null,
    elevationGainPlanned = null,
    velocityPlanned = null,

    // Metrics - Actual
    totalTime = null,
    distance = null,
    calories = null,
    energy = null,
    rpe = rpe,
    feeling = feeling,
    `if` = ifSource,
    tssSource = null,
    tssActual = null,
    normalizedPowerActual = null,
    powerAverage = null,
    powerMaximum = null,
    torqueAverage = null, // Novo campo da base
    torqueMaximum = null, // Novo campo da base
    heartRateAverage = null,
    heartRateMaximum = null,
    heartRateMinimum = null,
    cadenceAverage = null,
    cadenceMaximum = null,
    elevationGain = null,
    elevationLoss = null,
    elevationAverage = null,
    elevationMaximum = null,
    elevationMinimum = null,
    velocityAverage = null,
    velocityMaximum = null,
    normalizedSpeedActual = null,

    // Compliance & Meta
    complianceTssPercent = null,
    complianceDurationPercent = null,
    complianceDistancePercent = null,
    personalRecordCount = null,
    orderOnDay = null,
    userTags = null,
    isItAnOr = null,
    completed = null,
    isLocked = null,
    isHidden = null,
    publicSettingValue = null,
    sharedWorkoutInformationKey = null,
    sharedWorkoutInformationExpireKey = null,
    workoutDeviceSource = null,
    hasPrivateWorkoutNoteForCaller = null,
    code = null, // Novo campo da base

    // IDs and Equipment
    workoutSubTypeId = null,
    equipmentBikeId = null,
    equipmentShoeId = null,
    poolLengthOptionId = null,

    // Complex Arrays and Objects
    structure = structure,
    workoutComments = emptyList(),

    // Temperatures
    tempMax = null,
    tempMin = null,
    tempAvg = null,

    syncedTo = emptyList(),
    newComment = null,

    // Customizations
    distanceCustomized = null,
    distanceUnitsCustomized = null
)