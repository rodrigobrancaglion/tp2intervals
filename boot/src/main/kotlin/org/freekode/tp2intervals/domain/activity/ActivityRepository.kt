package org.freekode.tp2intervals.domain.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import java.time.LocalDate

interface ActivityRepository {
    fun platform(): Platform

    fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity>

    fun saveActivities(activities: List<Activity>, types: List<BaseType>)
}
