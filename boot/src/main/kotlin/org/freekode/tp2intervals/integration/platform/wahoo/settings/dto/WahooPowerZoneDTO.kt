package org.freekode.tp2intervals.integration.platform.wahoo.settings.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WahooPowerZoneDTO(
    val id: Long?,
    val ftp: Int?,
    @JsonProperty("workout_type_id")
    val workoutTypeId: Int?,
    // Add other fields if necessary
)