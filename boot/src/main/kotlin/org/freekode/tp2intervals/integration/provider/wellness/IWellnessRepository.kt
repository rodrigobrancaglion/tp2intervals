package org.freekode.tp2intervals.integration.provider.wellness

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.wellness.Wellness
import java.time.LocalDate

interface IWellnessRepository {
    fun platform(): Platform

    fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness>

    fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate)
}