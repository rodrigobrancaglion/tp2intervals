package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.workout.Workout
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TPPowerCalculationService(
) {
    /**
     * Extracts the primary Power Zone group and its threshold (FTP).
     * @return Pair of FTP and the list of individual zones.
     */
    fun getPowerInfo(settings: TPSettingsResponseDTO): Pair<Int, List<TPZoneRangeDTO>> {
        // Usually, workoutTypeId 0 is the default/global power zone
        val powerGroup = settings.powerZones.firstOrNull { it.workoutTypeId == 0 }
            ?: throw IllegalStateException("Power zones not found")

        return Pair(powerGroup.threshold.toInt(), powerGroup.zones)
    }

    /**
     * Calculates the target intensity string based on planned IF and FTP settings.
     * Returns a formatted string with duration, power range, and zone description.
     */
    fun getTargetIntensity(workout: Workout, settings: TPSettingsResponseDTO): String {
        // If IF is missing, we use 0.0 as a safe fallback,
        // but ideally, we should log or handle this case.
        val targetIf = workout.details.ifPlanned ?: 0.0
        val duration = workout.details.duration ?: Duration.ZERO

        // Check if we have enough data to show a meaningful intensity
        if (targetIf == 0.0 && duration == Duration.ZERO) {
            return "" // Or a default message like "Planned Intensity: N/A"
        }

        val durationStr = duration.toReadableString()
        val (ftp, zones) = getPowerInfo(settings)

        // REAL TARGET CALCULATION
        val targetWatts = (targetIf * ftp).toInt()

        val powerRange = getPrecisionPowerRange(targetWatts)
        val zoneInfo = getZoneDescription(targetWatts, zones)

        return "- $durationStr $powerRange ($zoneInfo)"
    }

    fun Duration.toReadableString(): String {
        val hours = this.toHours()
        val minutes = this.toMinutesPart()

        return when {
            hours > 0 && minutes > 0 -> "${hours}h${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    private fun getPrecisionPowerRange(targetWatts: Int): String {
        // PRECISION FORMAT:
        // We use "targetWatts" as the center of a small range (e.g., +/- 2%)
        // This forces Intervals to calculate the correct TSS.
        val rangeMin = targetWatts - 5
        val rangeMax = targetWatts + 5
        return "${rangeMin}-${rangeMax}W"
    }

    private fun getZoneDescription(targetWatts: Int, zones: List<TPZoneRangeDTO>): String {
        val targetDouble = targetWatts.toDouble()
        val zoneIndex = zones.indexOfFirst { targetDouble >= it.minimum && targetDouble <= it.maximum }

        if (zoneIndex == -1) return "Unknown Zone"

        val prefix = "Z${zoneIndex + 1}"
        val label = zones[zoneIndex].label

        return "$prefix - $label"
    }
}