package org.freekode.tp2intervals.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.dto.schedule.Schedulable
import org.freekode.tp2intervals.integration.provider.schedule.IScheduleRequestRepository
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WorkoutScheduledJobService(
    private val workoutService: WorkoutService,
    private val iScheduleRequestRepository: IScheduleRequestRepository,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun addRequest(schedulable: Schedulable) {
        val requestJson = objectMapper.writeValueAsString(schedulable)
        if (iScheduleRequestRepository.findByRequestJson(requestJson) != null) throw IllegalArgumentException("Request already exists")
        iScheduleRequestRepository.save(ScheduleRequestEntity(requestJson))
    }

    fun getRequests() =
        iScheduleRequestRepository.findAll()
            .filter { record ->
                try {
                    val request = objectMapper.readValue(record.requestJson, C2CTodayScheduledRequest::class.java)
                    request.hasType<TrainingType>()
                } catch (e: Exception) {
                    false
                }
            }
            .toList()

    fun deleteRequest(id: Int) {
        iScheduleRequestRepository.deleteById(id)
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    fun job() {
        val requests = getRequests().map { it.toSchedulable() }
        log.info("Starting processing scheduled requests. There are ${requests.size} requests")

        for (request in requests) {
            handleCopyCalendarToCalendarRequest(request)
        }

        log.info("Finished processing scheduled requests")
    }

    private fun handleCopyCalendarToCalendarRequest(request: C2CTodayScheduledRequest) {
        workoutService.copyWorkoutsC2C(request.forToday())
    }

    private fun ScheduleRequestEntity.toSchedulable(): C2CTodayScheduledRequest {
        return objectMapper.readValue(requestJson, C2CTodayScheduledRequest::class.java)
    }
}