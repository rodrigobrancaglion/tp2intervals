package org.freekode.tp2intervals.dto.workout

import config.BaseSpringITConfig
import org.assertj.core.api.Assertions.assertThat
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.dto.schedule.C2CTodayScheduledRequest
import org.freekode.tp2intervals.model.schedule.ScheduleRequestEntity
import org.freekode.tp2intervals.service.ScheduledJobService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WorkoutJobSchedulerIT : BaseSpringITConfig() {
    @Autowired
    lateinit var scheduledJob: ScheduledJobService

    @Test
    fun test() {
        val request =
            C2CTodayScheduledRequest(listOf(TrainingType.BIKE), true, Platform.INTERVALS, Platform.TRAINING_PEAKS)
        scheduledJob.addRequest(request, "TRAINING_PEAKS")

        val requests = scheduledJob.getRequests<TrainingType>("TRAINING_PEAKS")

        assertThat(requests.isNotEmpty()).isTrue()
        assertThat(requests[0] is ScheduleRequestEntity).isTrue()
    }
}
