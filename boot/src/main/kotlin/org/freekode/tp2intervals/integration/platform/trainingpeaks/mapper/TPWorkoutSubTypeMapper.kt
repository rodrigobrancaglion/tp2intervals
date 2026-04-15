package org.freekode.tp2intervals.integration.platform.trainingpeaks.mapper

import org.freekode.tp2intervals.domain.TrainingType

class TPWorkoutSubTypeMapper {
    companion object Companion {

        private val typeMap = mapOf(
            TrainingType.BIKE to 3,         // Road Bike
            TrainingType.GRAVEL_BIKE to 4,  // Gravel Bike
            TrainingType.VIRTUAL_BIKE to 49 // Virtual Bike
//            "Track Bike" to 5,
//            "Indoor Bike" to 6,
//            "Cyclocross" to 7,
//            "Hand Cycling" to 9,
//            "Time Trial" to 47,
        )

        fun getByWorkoutType(type: TrainingType): Int? = typeMap[type] ?: typeMap[TrainingType.BIKE]

        fun getWorkoutTypeFromId(id: Int?): TrainingType {
            return typeMap.entries
                .find { it.value == id }
                ?.key ?: TrainingType.BIKE
        }
    }
}