package org.freekode.tp2intervals.integration.platform.trainerroad.activity

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.utils.Base64
import org.springframework.core.io.Resource

class TrainerRoadActivityMapper {
    fun mapToActivity(dto: TrainerRoadActivityDTO, resource: Resource): Activity {
        val type = if (dto.completedRide!!.IsOutside) TrainingType.BIKE else TrainingType.VIRTUAL_BIKE

        return Activity(
            0,
            dto.completedRide.Date,
            type,
            dto.completedRide.Name,
            null,
            Base64.encodeToString(resource),
            null,
            null,
            null,
        )
    }
}
