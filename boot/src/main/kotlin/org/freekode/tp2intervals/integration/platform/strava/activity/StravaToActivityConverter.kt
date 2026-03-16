package org.freekode.tp2intervals.integration.platform.strava.activity

import org.freekode.tp2intervals.domain.activity.Activity

/**
 * Converts a Strava ActivityResponseDTO to the domain Activity model.
 */
class StravaToActivityConverter {

    fun toDomain(dto: ActivityResponseDTO): Activity {
        return Activity(
            workoutId = dto.id,
            startedAt = dto.start_date_local,
            type = dto.mapType(),
            title = dto.name,
            resource = null,
            fileName = null,
            rpe = null,
            feel = null,
        )
    }
}
