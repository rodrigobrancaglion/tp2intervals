package org.freekode.tp2intervals.infrastructure.platform.intervalsicu.activity

import org.freekode.tp2intervals.domain.ActivitiesType
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.infrastructure.platform.intervalsicu.IntervalsActivityDTO
import org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.TPTrainingFeelingMapper

class IntervalsToActivityConverter
{
    fun toDomain(activityDTO: IntervalsActivityDTO, pairedWorkout: Workout?): Activity {
        return Activity(
            pairedWorkout?.details?.externalData?.trainingPeaksId?.toLong() ?: 0,
            activityDTO.start_date_local,
            activityDTO.mapType(),
            activityDTO.name,
            null,
            activityDTO.icu_rpe,
            activityDTO.feel,
        )
    }

    /**
     * Converts a domain Activity to an IntervalsActivityDTO based on allowed update types.
     */
    fun toDTO(activityDTO: Activity, types: List<BaseType>): IntervalsActivityDTO {
        // Check if RPE should be updated, otherwise default to 1
        val rpe = if (types.containsType(ActivitiesType.RPE)) {
            activityDTO.rpe
        } else {
            1
        }

        // Check if FEEL should be updated, performing scale conversion if necessary
        val feel = if (types.containsType(ActivitiesType.FEEL)) {
            val feelingType = TPTrainingFeelingMapper.getByTPValue(activityDTO.feel)
            TPTrainingFeelingMapper.getICUValue(feelingType)
        } else {
            3
        }

        return IntervalsActivityDTO(rpe, feel)
    }

    /**
     * Extension to check if a specific ActivitiesType is present in the string list.
     */
    private fun List<BaseType>.containsType(type: ActivitiesType): Boolean {
        return this.contains(type)
    }
}
