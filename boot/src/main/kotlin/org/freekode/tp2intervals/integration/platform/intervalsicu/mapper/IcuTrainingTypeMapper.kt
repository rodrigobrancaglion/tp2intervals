package org.freekode.tp2intervals.integration.platform.intervalsicu.mapper

import org.freekode.tp2intervals.domain.TrainingType

class IcuTrainingTypeMapper {
    //Sport - Type
    companion object Companion {

        private val categoryMap = mapOf(
            TrainingType.BIKE to "Ride",
            TrainingType.MTB to "MountainBikeRide",
            TrainingType.GRAVEL_BIKE to "GravelRide",
            TrainingType.VIRTUAL_BIKE to "VirtualRide",
            TrainingType.RACE to "RACE_A",
            TrainingType.RUN to "Run",
            TrainingType.SWIM to "Swim",
            TrainingType.STRENGTH to "WeightTraining",
            TrainingType.WORKOUT to "Workout",
            TrainingType.NOTE to "NOTE",
            TrainingType.UNKNOWN to "Other",
            TrainingType.WALK to "Walk",

            // data only sync
            TrainingType.BRICK to "NOTE",
            TrainingType.DAY_OFF to "NOTE",
        )

        fun getByValue(value: String?): TrainingType {
            // Search the map for the specific VALUE (e.g., "GravelRide") and return the corresponding KEY (e.g., WorkoutType.GRAVEL_BIKE)
            return categoryMap.entries
                .find { it.value.equals(value, ignoreCase = true) }
                ?.key ?: TrainingType.getByValue(value) // Se não achar no map, tenta o fallback do Enum
        }

        fun getByType(trainingType: TrainingType?): String = categoryMap[trainingType] ?: "Other"
    }
}