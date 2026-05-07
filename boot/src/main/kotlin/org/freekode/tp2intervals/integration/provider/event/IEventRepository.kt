package org.freekode.tp2intervals.integration.provider.event

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.event.Event
import java.time.LocalDate

interface IEventRepository {
    fun platform(): Platform

    fun getEvents(startDate: LocalDate, endDate: LocalDate): List<Event>

    fun saveEvents(events: List<Event>)
}
