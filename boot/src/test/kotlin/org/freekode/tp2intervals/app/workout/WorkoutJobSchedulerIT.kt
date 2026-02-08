package org.freekode.tp2intervals.dto.workout

import config.BaseSpringITConfig
import org.assertj.core.api.Assertions.assertThat
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.freekode.tp2intervals.service.WorkoutScheduledJobService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WorkoutJobSchedulerIT : BaseSpringITConfig() {
    @Autowired
    lateinit var workoutScheduledJobService: WorkoutScheduledJobService

    @Test
    fun test() {
        val request =
            C2CTodayScheduledRequest(listOf(TrainingType.BIKE), true, Platform.INTERVALS, Platform.TRAINING_PEAKS)
        workoutScheduledJobService.addRequest(request)

        val requests = workoutScheduledJobService.getRequests()

        assertThat(requests.isNotEmpty()).isTrue()
        assertThat(requests[0] is ScheduleRequestEntity).isTrue()
    }
}
