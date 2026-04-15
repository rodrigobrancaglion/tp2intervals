package org.freekode.tp2intervals.integration.platform.rouvy.activity

import org.freekode.tp2intervals.aspect.LogRepository
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Repository
class RouvyActivityRepository(
    private val rouvySyncManager: RouvySyncManager
) : IActivityRepository {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun platform() = Platform.ROUVY

    @LogRepository
    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {

        val rouvyActiviteisInfo = rouvySyncManager.getActivitiesInDateRange(startDate, endDate)

        val activiteisInfo = mutableListOf<Activity>()

        rouvyActiviteisInfo.forEach { rouvyActivityInfo ->
            val activityDate = rouvyActivityInfo.date ?: LocalDate.now()

            if (activityDate.isBefore(startDate) || activityDate.isAfter(endDate)) {
                log.info("Latest Rouvy activity at $activityDate is outside requested range [$startDate, $endDate]")
                return emptyList()
            }

            val activityId = extractWorkoutId(rouvyActivityInfo.id)

            // Use Route Name if available for a better Strava/Intervals experience
            val finalTitle = if (!rouvyActivityInfo.routeName.isNullOrBlank()) {
                "ROUVY | ${rouvyActivityInfo.routeName}"
            } else {
                if (rouvyActivityInfo.name.startsWith("ROUVY")) rouvyActivityInfo.name else "ROUVY | ${rouvyActivityInfo.name}"
            }

            activiteisInfo.add(Activity(
                workoutId = activityId,
                startedAt = activityDate.atTime(LocalTime.MIDNIGHT),
                type = null,
                title = finalTitle,
                description = rouvyActivityInfo.name,
                resource = if (rouvyActivityInfo.fitBytes != null) Base64.getEncoder().encodeToString(rouvyActivityInfo.fitBytes) else null,
                fileName = "${rouvyActivityInfo.id}.fit",
                rpe = null,
                feel = null
            ))

            log.info("Rouvy activity ready for sync: id=$activityId, title=$finalTitle, date=$activityDate")
        }

        return activiteisInfo
    }

    private fun extractWorkoutId(rouvyId: String): Long {
        // Extract the numeric part (YYMMDDHHMMSS) from the Rouvy ID
        val numericPart = rouvyId.split("-").firstOrNull()?.filter { it.isDigit() }
        return numericPart?.toLongOrNull() ?: rouvyId.hashCode().toLong().let { if (it < 0) -it else it }
    }

    @LogRepository
    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        // Rouvy is read-only in this application
        log.info("Rouvy saveActivities called but not implemented (read-only source)")
    }
}
