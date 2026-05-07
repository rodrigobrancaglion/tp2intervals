package org.freekode.tp2intervals.domain.event

import org.freekode.tp2intervals.domain.EventType
import org.freekode.tp2intervals.domain.OtherType
import java.time.LocalDate

/**
 * Represents a race/event from any platform.
 * category maps to ICU categories: RACE_A, RACE_B, RACE_C.
 * When the user selects RACE on the frontend, the BE defaults to RACE_A for new events.
 */
data class Event(
    val externalId: String,
    val name: String,
    val date: LocalDate,
    val category: OtherType,
    val eventType: EventType?,
    val subEventType: String?,
    val description: String?,
    val durationSeconds: Long?,
    val distance: Double?,
    val tss: Int?,
)
