package org.freekode.tp2intervals.integration.provider.settings

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import java.time.LocalDate

interface ISettingsRepository {
    fun platform(): Platform

    fun get(startDate: LocalDate, endDate: LocalDate): List<Activity>

    fun save(activities: List<Activity>, types: List<BaseType>)
}
