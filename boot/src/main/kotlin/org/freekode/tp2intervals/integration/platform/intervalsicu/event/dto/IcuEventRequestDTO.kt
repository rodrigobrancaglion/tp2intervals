package org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Payload for POST /api/v1/athlete/{athleteId}/events/bulk
 * Confirmed ICU API payload format:
 * [{"category":"RACE_A","name":"...","description":"...","type":"Ride",
 *   "moving_time":7200,"attachments":[],"start_date_local":"2026-05-05T00:00:00",
 *   "end_date_local":null,"entered":false,"indoor":false}]
 */
data class IcuEventRequestDTO(
    @JsonProperty("category") val category: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("type") val type: String,
    @JsonProperty("moving_time") val moving_time: Long?,
    @JsonProperty("distance") val distance: Double?,
    @JsonProperty("attachments") val attachments: List<Any> = emptyList(),
    @JsonProperty("start_date_local") val start_date_local: String,
    @JsonProperty("end_date_local") val end_date_local: String? = null,
    @JsonProperty("entered") val entered: Boolean = false,
    @JsonProperty("indoor") val indoor: Boolean = false,
    @JsonProperty("icu_training_load") val icu_training_load: Int? = null,
)