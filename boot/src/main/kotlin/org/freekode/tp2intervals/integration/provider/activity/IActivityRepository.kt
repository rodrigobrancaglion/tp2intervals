package org.freekode.tp2intervals.integration.provider.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import java.time.LocalDate

interface IActivityRepository {
    fun platform(): Platform

    fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity>

    fun saveActivities(activities: List<Activity>, types: List<BaseType>)
}