package org.freekode.tp2intervals.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.aspect.LogJob
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.*
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.dto.schedule.Schedulable
import org.freekode.tp2intervals.integration.provider.schedule.IScheduleRequestRepository
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.freekode.tp2intervals.model.user.UserRepository
import org.freekode.tp2intervals.utils.UserContextHolder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ScheduledJobService(
    private val workoutService: WorkoutService,
    private val wellnessService: WellnessService,
    private val activityService: ActivityService,
    private val eventService: EventService,
    private val settingService: SettingService,
    val IScheduleRequestRepository: IScheduleRequestRepository,
    val objectMapper: ObjectMapper,
    private val userRepository: UserRepository
) {
    private val logger = AppLogger.get(this.javaClass)

    fun addRequest(schedulable: Schedulable, platform: String) {
        val requestJson = objectMapper.writeValueAsString(schedulable)
        val username = UserContextHolder.username
        val existing = IScheduleRequestRepository.findByPlatformAndUsername(platform, username)
            .firstOrNull { it.requestJson == requestJson }
        if (existing != null) throw IllegalArgumentException("Request already exists")
        IScheduleRequestRepository.save(ScheduleRequestEntity(null, requestJson, platform, username))
    }

    final inline fun <reified T : Enum<T>> getRequests(platform: String): List<ScheduleRequestEntity> {
        val username = UserContextHolder.username
        return IScheduleRequestRepository.findByPlatformAndUsername(platform, username)
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
    }

    fun deleteRequest(id: Int) {
        IScheduleRequestRepository.deleteById(id)
    }

    private fun runForEveryUser(block: () -> Unit) {
        val users = userRepository.findAll()
        for (user in users) {
            UserContextHolder.username = user.username
            try {
                block()
            } catch (e: Exception) {
                logger.errorL2In("Error processing scheduled tasks for user ${user.username}: ${e.message}", e)
            } finally {
                UserContextHolder.clear()
            }
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobWorkout() {
        runForEveryUser {
            val username = UserContextHolder.username
            val requests = IScheduleRequestRepository.findByUsername(username)
                .filter { record ->
                    try {
                        val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                        request.hasType<TrainingType>()
                    } catch (e: Exception) { false }
                }
                .map { it.toSchedulable() }
            logger.infoL4In("Starting processing scheduled [WORKOUT]. Requests for user: $username - There are ${requests.size} requests")

            for (request in requests) {
                workoutService.copyWorkoutsC2C(request.forToday())
            }
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobWellness() {
        runForEveryUser {
            val username = UserContextHolder.username
            val requests = IScheduleRequestRepository.findByUsername(username)
                .mapNotNull { record ->
                    try {
                        val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                        if (request.hasType<WellnessType>()) request else null
                    } catch (e: Exception) { null }
                }

            logger.infoL4In("Starting processing scheduled [WELLNESS]. Requests for user: $username - There are ${requests.size} requests")
            for (request in requests) {
                wellnessService.copyWellnessC2C(request.forToday())
            }
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobActivities() {
        runForEveryUser {
            val username = UserContextHolder.username
            val requests = IScheduleRequestRepository.findByUsername(username)
                .mapNotNull { record ->
                    try {
                        val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                        if (request.hasType<ActivityType>()) request else null
                    } catch (e: Exception) { null }
                }

            logger.infoL4In("Starting processing scheduled [ACTIVITY]. Requests for user: $username - There are ${requests.size} requests")
            for (request in requests) {
                activityService.syncActivities(request.forToday())
            }
        }
    }

    @LogJob
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobEvents() {
        runForEveryUser {
            val username = UserContextHolder.username
            val requests = IScheduleRequestRepository.findByUsername(username)
                .mapNotNull { record ->
                    try {
                        val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                        if (request.hasType<OtherType>()) request else null
                    } catch (e: Exception) { null }
                }

            logger.infoL4In("Starting processing scheduled [EVENT]. Requests for user: $username - There are ${requests.size} requests")
            for (request in requests) {
                eventService.syncEvents(request.forToday())
            }
        }
    }

    @LogJob
    @Scheduled(cron = "0 0 22 ? * MON")
    fun scheduledPowerZoneSync() {
        runForEveryUser {
            val username = UserContextHolder.username
            if (settingService.isSchedulerEnabled()) {
                logger.infoL2In("Running scheduled Power-zone sync (Monday) for user $username")
                settingService.syncPowerZones(Platform.TRAINING_PEAKS, Platform.INTERVALS)
            } else {
                logger.infoL2In("Scheduled Power-zone sync is disabled for user $username")
            }
        }
    }

    private fun ScheduleRequestEntity.toSchedulable(): C2CTodayScheduledRequest {
        return objectMapper.readValue(requestJson, C2CTodayScheduledRequest::class.java)
    }
}