package org.freekode.tp2intervals.domain.activity

import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.workout.WorkoutComment
import org.freekode.tp2intervals.integration.utils.Base64
import org.springframework.core.io.Resource
import java.time.LocalDateTime

data class Activity(
    val workoutId: Long,
    val startedAt: LocalDateTime?,
    val type: TrainingType?,
    val title: String?,
    val description: String?,
    val resource: String?,
    val fileName: String?,
    val rpe: Int?,
    val feel: Int?,
    val deviceProductName: String? = null,
    var workoutComments: List<WorkoutComment> = emptyList(),
) {
    constructor(workoutId: Long, rpe: Int?, feel: Int?) : this(workoutId, null, null, null, null, null, null, rpe, feel)

    fun withResource(resource: Resource, fileName: String) =
        Activity(workoutId, startedAt, type, title, null, Base64.encodeToString(resource), fileName, null, null)

    /**
     * Resets metrics based on requested sync types.
     */
    fun filterActivity(requestedTypes: List<BaseType>): Activity? {
        // Se o ID for inválido, retornamos NULL para que o mapNotNull ignore este item
        if (this.workoutId.toInt() == 0) {
            return null
        }

        return this.copy(
            rpe = if (requestedTypes.contains(ActivityType.RPE)) this.rpe else null,
            feel = if (requestedTypes.contains(ActivityType.FEEL)) this.feel else null
        )
    }

    override fun toString(): String {
        return "workoutId=$workoutId " +
                "startedAt:$startedAt " +
                "type:$type " +
                "title:$title " +
                "description:$description " +
                "resource:- " +
                "fileName:$fileName " +
                "rpe:$rpe " +
                "feel:$feel " +
                "deviceProductName:$deviceProductName "
    }
}
