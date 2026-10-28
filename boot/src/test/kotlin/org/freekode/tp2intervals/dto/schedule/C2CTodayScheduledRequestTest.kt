package org.freekode.tp2intervals.dto.schedule

import org.assertj.core.api.Assertions.assertThat
import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.OtherType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.TrainingType
import org.junit.jupiter.api.Test

class C2CTodayScheduledRequestTest {

    @Test
    fun `should identify event request when types contain RACE and NOTES`() {
        val request = C2CTodayScheduledRequest(
            types = listOf(TrainingType.RACE, ActivityType.NOTES),
            skipSynced = false,
            sourcePlatform = Platform.TRAINING_PEAKS,
            targetPlatform = Platform.INTERVALS
        )

        assertThat(request.hasType<OtherType>()).isTrue()
        assertThat(request.hasType<TrainingType>()).isFalse()
        assertThat(request.hasType<ActivityType>()).isFalse()
    }

    @Test
    fun `should identify workout request when types contain regular training types`() {
        val request = C2CTodayScheduledRequest(
            types = listOf(TrainingType.BIKE, TrainingType.RUN),
            skipSynced = false,
            sourcePlatform = Platform.TRAINING_PEAKS,
            targetPlatform = Platform.INTERVALS
        )

        assertThat(request.hasType<TrainingType>()).isTrue()
        assertThat(request.hasType<OtherType>()).isFalse()
        assertThat(request.hasType<ActivityType>()).isFalse()
    }

    @Test
    fun `should identify activity request when types contain regular activity types`() {
        val request = C2CTodayScheduledRequest(
            types = listOf(ActivityType.ACTIVITY, ActivityType.RPE),
            skipSynced = false,
            sourcePlatform = Platform.TRAINING_PEAKS,
            targetPlatform = Platform.INTERVALS
        )

        assertThat(request.hasType<ActivityType>()).isTrue()
        assertThat(request.hasType<OtherType>()).isFalse()
        assertThat(request.hasType<TrainingType>()).isFalse()
    }

    @Test
    fun `should identify event request when types contain OtherType directly`() {
        val request = C2CTodayScheduledRequest(
            types = listOf(OtherType.RACE_A),
            skipSynced = false,
            sourcePlatform = Platform.TRAINING_PEAKS,
            targetPlatform = Platform.INTERVALS
        )

        assertThat(request.hasType<OtherType>()).isTrue()
        assertThat(request.hasType<TrainingType>()).isFalse()
        assertThat(request.hasType<ActivityType>()).isFalse()
    }
}
