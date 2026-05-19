package org.freekode.tp2intervals.integration.provider.settings

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.settings.PowerZone

interface ISettingsRepository {
    fun platform(): Platform

    fun getPowerZones(): Pair<Int, List<PowerZone>>

    fun savePowerZones(threshold: Int, zones: List<PowerZone>)
}
