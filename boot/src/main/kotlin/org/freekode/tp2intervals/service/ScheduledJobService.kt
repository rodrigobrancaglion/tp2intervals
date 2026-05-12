package org.freekode.tp2intervals.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.aspect.LogJob
import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.OtherType
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.dto.schedule.Schedulable
import org.freekode.tp2intervals.integration.provider.schedule.IScheduleRequestRepository
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.freekode.tp2intervals.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ScheduledJobService(
    private val workoutService: WorkoutService,
    private val wellnessService: WellnessService,
    private val activityService: ActivityService,
    private val eventService: EventService,
    val IScheduleRequestRepository: IScheduleRequestRepository,
    val objectMapper: ObjectMapper,
    val jdbcTemplate: JdbcTemplate
) {
    private val log = LoggerFactory.getLogger(this.javaClass)


//    @PostConstruct
//    fun migratePlatformColumn() {
//        try {
//            // Add column if it doesn't exist (SQLite doesn't support IF NOT EXISTS on columns)
//            // PRAGMA returns one row per column; check if 'platform' is present
//            val hasPlatformColumn = jdbcTemplate.queryForList("PRAGMA table_info(schedule_requests)")
//                .any { row -> row["name"] == "platform" }
//
//            if (!hasPlatformColumn) {
//                log.info("Adding 'platform' column to schedule_requests table")
//                jdbcTemplate.execute("ALTER TABLE schedule_requests ADD COLUMN platform TEXT")
//            }
//
//            // Migrate existing rows with NULL platform to TRAINING_PEAKS
//            val updated = jdbcTemplate.update(
//                "UPDATE schedule_requests SET platform = 'TRAINING_PEAKS' WHERE platform IS NULL"
//            )
//            if (updated > 0) {
//                log.info("Migrated $updated existing schedule(s) to platform=TRAINING_PEAKS")
//            }
//        } catch (e: Exception) {
//            log.error("Error during schedule_requests platform migration: ${e.message}", e)
//        }
//    }

    fun addRequest(schedulable: Schedulable, platform: String) {
        val requestJson = objectMapper.writeValueAsString(schedulable)
        val existing = IScheduleRequestRepository.findByPlatform(platform)
            .firstOrNull { it.requestJson == requestJson }
        if (existing != null) throw IllegalArgumentException("Request already exists")
        IScheduleRequestRepository.save(ScheduleRequestEntity(requestJson, platform))
    }

    final inline fun <reified T : Enum<T>> getRequests(platform: String) =
        IScheduleRequestRepository.findByPlatform(platform)
            .mapNotNull { record ->
                try {
                    val request = objectMapper.readValue(
                        record.requestJson,
                        C2CTodayScheduledRequest::class.java
                    )
                    if (request.hasType<T>()) record else null
                } catch (e: Exception) {
                    null
                }
            }

    fun deleteRequest(id: Int) {
        IScheduleRequestRepository.deleteById(id)
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobWorkout() {
        val requests = IScheduleRequestRepository.findAll()
            .filter { record ->
                try {
                    val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                    request.hasType<TrainingType>()
                } catch (e: Exception) { false }
            }
            .map { it.toSchedulable() }
        log.info("${Constants.logStringIndentation}Starting processing scheduled [WORKOUT] requests. There are ${requests.size} requests")

        for (request in requests) {
            workoutService.copyWorkoutsC2C(request.forToday())
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobWellness() {
        val requests = IScheduleRequestRepository.findAll()
            .mapNotNull { record ->
                try {
                    val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                    if (request.hasType<WellnessType>()) request else null
                } catch (e: Exception) { null }
            }

        log.info("${Constants.logStringIndentation}Starting processing scheduled [WELLNESS] requests. There are ${requests.size} requests")
        for (request in requests) {
            wellnessService.copyWellnessC2C(request.forToday())
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobActivities() {
        val requests = IScheduleRequestRepository.findAll()
            .mapNotNull { record ->
                try {
                    val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                    if (request.hasType<ActivityType>()) request else null
                } catch (e: Exception) { null }
            }

        log.info("${Constants.logStringIndentation}Starting processing scheduled [ACTIVITY] requests. There are ${requests.size} requests")
        for (request in requests) {
            activityService.syncActivities(request.forToday())
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobEvents() {
        val requests = IScheduleRequestRepository.findAll()
            .mapNotNull { record ->
                try {
                    val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                    if (request.hasType<OtherType>()) request else null
                } catch (e: Exception) { null }
            }

        log.info("${Constants.logStringIndentation}Starting processing scheduled [EVENT] requests. There are ${requests.size} requests")
        for (request in requests) {
            eventService.syncEvents(request.forToday())
        }
    }

    private fun ScheduleRequestEntity.toSchedulable(): C2CTodayScheduledRequest {
        return objectMapper.readValue(requestJson, C2CTodayScheduledRequest::class.java)
    }
}