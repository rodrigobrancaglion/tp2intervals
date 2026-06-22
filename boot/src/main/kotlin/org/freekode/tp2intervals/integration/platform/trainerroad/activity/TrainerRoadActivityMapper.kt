package org.freekode.tp2intervals.integration.platform.trainerroad.activity

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.trainerroad.activity.dto.TrainerRoadActivityDTO
import org.freekode.tp2intervals.integration.utils.Base64
import org.springframework.core.io.Resource

class TrainerRoadActivityMapper {
    fun mapToActivity(dto: TrainerRoadActivityDTO, resource: Resource): Activity {
        val completedRide = dto.completedRide
        val type = if (completedRide?.IsOutside ?: (dto.isOutside == true)) {
            TrainingType.BIKE
        } else {
            TrainingType.VIRTUAL_BIKE
        }

        return Activity(
            0,
            completedRide?.Date ?: dto.date,
            type,
            completedRide?.Name ?: dto.name ?: "TrainerRoad activity",
            null,
            Base64.encodeToString(resource),
            null,
            null,
            null,
        )
    }
}
