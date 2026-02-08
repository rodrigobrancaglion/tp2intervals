package org.freekode.tp2intervals.domain

import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsTrainingTypeMapper
import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPWorkoutSubTypeMapper
import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPWorkoutTypeMapper

class PlatformTrainingMapper {
    companion object {

        fun mapTpToIntervals(workoutDetails: WorkoutDetails): MappedTraining {
            val category = when (workoutDetails.type) {
                TrainingType.DAY_OFF,
                TrainingType.BRICK -> CategoryType.NOTE.name
                TrainingType.RACE -> CategoryType.RACE_A.name

                /**
                 * Group all standard training types under the WORKOUT category
                 * WorkoutTypes: BIKE, VIRTUAL_BIKE, MTB, RUN, SWIM, STRENGTH
                 */
                else -> CategoryType.WORKOUT.name
            }

            val type = when {
                // If it's BIKE, we check sub-types to decide between Ride, Gravel, or Virtual
                workoutDetails.type == TrainingType.BIKE -> {
                    IntervalsTrainingTypeMapper.getByType(workoutDetails.workoutSubTypeId)
                }

                // For all other types (RUN, SWIM, etc.), use the direct type mapping
                else -> IntervalsTrainingTypeMapper.getByType(workoutDetails.type)
            }

            return MappedTraining(
                category = category,
                typeName = type
            )
        }

        fun mapIntervalsToTp(workoutDetails: WorkoutDetails): MappedTraining {
            val categoryId = TPWorkoutTypeMapper.getByType(workoutDetails.type)

            // Get the sub-type ID (if applicable) from the TPWorkoutSubTypeMapper
            val subTypeId = TPWorkoutSubTypeMapper.getByWorkoutType(workoutDetails.type)

            return MappedTraining(
                category = categoryId.toString(),
                typeName = subTypeId?.toString()
            )
        }
    }
}