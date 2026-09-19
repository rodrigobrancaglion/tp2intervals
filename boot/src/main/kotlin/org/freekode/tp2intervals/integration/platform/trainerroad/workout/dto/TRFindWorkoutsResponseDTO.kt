package org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto

import com.fasterxml.jackson.annotation.JsonProperty

class TRFindWorkoutsResponseDTO(
    @param:JsonProperty("Workouts")
    val workouts: List<TRWorkoutDetailsDTO>,
)