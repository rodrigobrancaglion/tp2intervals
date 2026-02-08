package org.freekode.tp2intervals.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.dto.schedule.Schedulable
import org.freekode.tp2intervals.integration.provider.schedule.IScheduleRequestRepository
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ScheduledJobService(
    private val wellnessService: WellnessService,
    private val activityService: ActivityService,
    val IScheduleRequestRepository: IScheduleRequestRepository,
    val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun addRequest(schedulable: Schedulable) {
        val requestJson = objectMapper.writeValueAsString(schedulable)
        if (IScheduleRequestRepository.findByRequestJson(requestJson) != null) throw IllegalArgumentException("Request already exists")
        IScheduleRequestRepository.save(ScheduleRequestEntity(requestJson))
    }

    final inline fun <reified T : Enum<T>> getRequests() =
        IScheduleRequestRepository.findAll()
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
        IScheduleRequestRepository.deleteById(id)
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobWellness() {
        val requests = getRequests<WellnessType>().map { it.toSchedulable() }

        log.info("Starting processing scheduled Wellness requests. There are ${requests.size} requests")
        for (request in requests) {
            handleCopyWellnessCalendarToCalendarRequest(request)
        }
        log.info("Finished processing scheduled Wellness requests")
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun jobActivities() {
        val requests = getRequests<ActivityType>().map { it.toSchedulable() }

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