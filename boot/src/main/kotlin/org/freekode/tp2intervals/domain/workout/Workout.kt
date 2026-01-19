package org.freekode.tp2intervals.domain.workout

import org.freekode.tp2intervals.domain.CategoryType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.workout.structure.WorkoutStructure
import java.io.Serializable
import java.time.LocalDateTime

data class Workout(
    val id: Long? = null,
    val details: WorkoutDetails,
    val date: LocalDateTime?,
    val structure: WorkoutStructure?,
) : Serializable {

    constructor(details: WorkoutDetails,
                date: LocalDateTime?,
                structure: WorkoutStructure?,) : this(null, details, date, structure)

    companion object {
        fun note(date: LocalDateTime, name: String, description: String?, externalData: ExternalData): Workout {
            return Workout(WorkoutDetails(TrainingType.NOTE, name, description, null, null, null, externalData), date, null)
        }
    }

    fun withDate(date: LocalDateTime): Workout {
        return Workout(details, date, structure)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workout

        return details == other.details
    }

    override fun hashCode(): Int {
        return details.hashCode()
    }

    /**
     * Checks if the workout belongs to the WORKOUT category.
     * Used to determine if it should have structure/intensity calculations.
     */
    fun isWorkoutCategory(): Boolean {
        return this.details.type.category == CategoryType.WORKOUT
    }
}
