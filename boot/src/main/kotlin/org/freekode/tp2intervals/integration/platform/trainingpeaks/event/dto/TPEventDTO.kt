package org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * Clean and professional DTO for TP Event.
 * Includes all necessary fields with strict order and null-handling for the V6 API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "goals", "atpPriority", "legs", "eventDate", "name", "personId",
    "eventType", "distance", "workouts", "results", "distanceUnits",
    "description", "raceTypeDuration"
)
data class TPEventDTO(
    @JsonProperty("id") val id: Long? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("goals") val goals: TPEventGoalsDTO? = TPEventGoalsDTO(),

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("atpPriority") val atpPriority: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("legs") val legs: List<Any>? = emptyList(),

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("eventDate") val eventDate: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("name") val name: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("personId") val personId: Long? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("eventType") val eventType: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("distance") val distance: Double? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("workouts") val workouts: List<Any>? = emptyList(),

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("results") val results: List<TPEventResultDTO>? = emptyList(),

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("distanceUnits") val distanceUnits: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("description") val description: String? = null,

    @get:JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("raceTypeDuration") val raceTypeDuration: String? = null,

    // Metadata / External fields (Omitted if null)
    @JsonProperty("comment") val comment: String? = null,
    @JsonProperty("externalEventSource") val externalEventSource: String? = null,
    @JsonProperty("externalEventId") val externalEventId: String? = null,
    @JsonProperty("atpId") val atpId: Long? = null,
    @JsonProperty("atpWeekId") val atpWeekId: Long? = null,
    @JsonProperty("ctlTarget") val ctlTarget: Double? = null,
    @JsonProperty("isHidden") val isHidden: Boolean? = null,
    @JsonProperty("isLocked") val isLocked: Boolean? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TPEventResultDTO(
    @JsonProperty("resultType") val resultType: String?,
    @JsonProperty("place") val place: Int? = null,
    @JsonProperty("time") val time: Double? = null,
    @JsonProperty("entrants") val entrants: Int? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TPEventGoalsDTO(
    @JsonProperty("athleteEventId") val athleteEventId: Long? = null,
    @JsonProperty("athleteId") val athleteId: Long? = null,
    @JsonProperty("distance") val distance: TPGoalValueDTO<Double>? = null,
    @JsonProperty("time") val time: TPGoalValueDTO<Double>? = null,
    @JsonProperty("place") val place: TPGoalValueDTO<Int>? = null,
    @JsonProperty("finish") val finish: TPGoalValueDTO<Boolean>? = null,
    @JsonProperty("pr") val pr: TPGoalValueDTO<Boolean>? = null,
    @JsonProperty("written") val written: List<TPGoalValueDTO<String>>? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TPGoalValueDTO<T>(
    @JsonProperty("value") val value: T?,
    @JsonProperty("displayUnits") val displayUnits: String? = null,
    @JsonProperty("complete") val complete: Boolean? = null,
    @JsonProperty("sortOrder") val sortOrder: Int? = null,
)
