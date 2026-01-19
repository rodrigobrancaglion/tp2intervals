package org.freekode.tp2intervals.domain.activity

import org.freekode.tp2intervals.domain.ActivitiesType
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.infrastructure.utils.Base64
import org.springframework.core.io.Resource
import java.time.LocalDateTime

data class Activity(
    val workoutId: Long,
    val startedAt: LocalDateTime,
    val type: TrainingType,
    val title: String?,
    val resource: String?,
    val rpe: Int?,
    val feel: Int?,
) {
    fun withResource(resource: Resource) =
        Activity(workoutId, startedAt, type, title, Base64.encodeToString(resource), null, null)

    /**
     * Resets metrics based on requested sync types.
     */
    fun filterActivity(requestedTypes: List<BaseType>): Activity {
        return this.copy(
            rpe = if (requestedTypes.contains(ActivitiesType.RPE)) this.rpe else null,
            feel = if (requestedTypes.contains(ActivitiesType.FEEL)) this.feel else null
        )
    }
}
