package org.freekode.tp2intervals.app.schedule

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.app.activity.ActivityService
import org.freekode.tp2intervals.app.wellness.WellnessService
import org.freekode.tp2intervals.domain.ActivitiesType
import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.infrastructure.schedule.ScheduleRequestEntity
import org.freekode.tp2intervals.infrastructure.schedule.ScheduleRequestRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ScheduledJobService(
    private val wellnessService: WellnessService,
    private val activityService: ActivityService,
    val scheduleRequestRepository: ScheduleRequestRepository,
    val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun addRequest(schedulable: Schedulable) {
        val requestJson = objectMapper.writeValueAsString(schedulable)
        if (scheduleRequestRepository.findByRequestJson(requestJson) != null) throw IllegalArgumentException("Request already exists")
        scheduleRequestRepository.save(ScheduleRequestEntity(requestJson))
    }

    final inline fun <reified T : Enum<T>> getRequests() =
        scheduleRequestRepository.findAll()
            .mapNotNull { record ->
                try {
                    val request = objectMapper.readValue(
                        record.requestJson,
                        C2CTodayScheduledRequest::class.java
                    )
                    // Retornamos a entidade (record) apenas se o hasType for true
                    if (request.hasType<T>()) record else null
                } catch (e: Exception) {
                    null
                }
            }

    fun deleteRequest(id: Int) {
        scheduleRequestRepository.deleteById(id)
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES)
    fun jobWellness() {
        val requests = getRequests<WellnessType>().map { it.toSchedulable() }

        log.info("Starting processing scheduled Wellness requests. There are ${requests.size} requests")
        for (request in requests) {
            handleCopyWellnessCalendarToCalendarRequest(request)
        }
        log.info("Finished processing scheduled Wellness requests")
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES)
    fun jobActivities() {
        val requests = getRequests<ActivitiesType>().map { it.toSchedulable() }

        log.info("Starting processing scheduled Activity requests. There are ${requests.size} requests")
        for (request in requests) {
            handleCopyActivitiesCalendarToCalendarRequest(request)
        }
        log.info("Finished processing scheduled Activity requests")
    }


    private fun handleCopyWellnessCalendarToCalendarRequest(request: C2CTodayScheduledRequest) {
        wellnessService.copyWellnessC2C(request.forToday())
    }

    private fun handleCopyActivitiesCalendarToCalendarRequest(request: C2CTodayScheduledRequest) {
        activityService.syncActivities(request.forToday())
    }

    private fun ScheduleRequestEntity.toSchedulable(): C2CTodayScheduledRequest {
        return objectMapper.readValue(requestJson, C2CTodayScheduledRequest::class.java)
    }
}