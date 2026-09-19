package org.freekode.tp2intervals.integration.provider.schedule

import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.springframework.data.repository.CrudRepository

interface IScheduleRequestRepository : CrudRepository<ScheduleRequestEntity, Int> {
    fun findByUsername(username: String): List<ScheduleRequestEntity>
    fun findByPlatformAndUsername(platform: String, username: String): List<ScheduleRequestEntity>
    fun findByRequestJsonAndUsername(requestJson: String, username: String): ScheduleRequestEntity?
}
