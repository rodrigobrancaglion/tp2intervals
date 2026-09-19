package org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class IcuFolder(
    val id: String,
    val type: String,
    val name: String,
    @JsonProperty("start_date_local")
    val startDateLocal: LocalDate?,
    val num_workouts: Int,
)