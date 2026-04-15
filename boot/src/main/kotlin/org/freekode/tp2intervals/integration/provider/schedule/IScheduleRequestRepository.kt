package org.freekode.tp2intervals.integration.provider.schedule

import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IScheduleRequestRepository : CrudRepository<ScheduleRequestEntity, Int> {
    fun findByRequestJson(requestJson: String): ScheduleRequestEntity?
    fun findByPlatform(platform: String): List<ScheduleRequestEntity>
}
