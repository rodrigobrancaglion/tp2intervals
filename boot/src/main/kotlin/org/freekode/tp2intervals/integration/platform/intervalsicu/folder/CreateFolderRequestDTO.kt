package org.freekode.tp2intervals.integration.platform.intervalsicu.folder

data class CreateFolderRequestDTO(
    val auto_rollout_day: Int,
    val name: String,
    val description: String,
    val rollout_weeks: Int,
    val start_date_local: String?,
    val starting_atl: Int,
    val starting_ctl: Int,
    val type: String,
)
