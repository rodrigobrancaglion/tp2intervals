package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.workout.structure.StepTarget
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IntervalsWorkoutDocDTO

class IntervalsToTargetConverter(
    private val ftp: Double?,
    private val lthr: Double?,
    private val paceThreshold: Double?
) {
    fun toMainTarget(stepDTO: IntervalsWorkoutDocDTO.WorkoutStepDTO): StepTarget {
        return when {
            // Priority 1: Use resolved values calculated by the server (Underscore fields)
            stepDTO._power != null -> mapSimpleUnit(ftp!!, stepDTO._power)
            stepDTO._hr != null -> mapSimpleUnit(lthr!!, stepDTO._hr)
            stepDTO._pace != null -> mapSimpleUnit(paceThreshold!!, stepDTO._pace)

            // Priority 2: Fallback to absolute Watts if the unit is "w"
            stepDTO.power?.units == "w" -> mapAbsolutePower(stepDTO.power)

            // Error: No valid target found for this step
            else -> throw PlatformException(
                Platform.INTERVALS,
                "Unknown target or missing metrics in step: ${stepDTO.text ?: "Unnamed Step"}"
            )
        }
    }

    fun toCadenceTarget(stepValueDTO: IntervalsWorkoutDocDTO.StepValueDTO): StepTarget {
        val (min, max) = if (stepValueDTO.value != null) {
            stepValueDTO.value to stepValueDTO.value
        } else {
            stepValueDTO.start!! to stepValueDTO.end!!
        }
        return StepTarget(min, max)
    }

    private fun mapSimpleUnit(
        thresholdValue: Double,
        resolvedStepValueDTO: IntervalsWorkoutDocDTO.ResolvedStepValueDTO
    ): StepTarget {
        val (min, max) = getRange(thresholdValue, resolvedStepValueDTO)
        return StepTarget(min, max)
    }

    private fun getRange(
        thresholdValue: Double,
        resolvedStepValueDTO: IntervalsWorkoutDocDTO.ResolvedStepValueDTO
    ): Pair<Int, Int> {
        val rangeStart = Math.round((resolvedStepValueDTO.start / thresholdValue) * 100).toInt()
        val rangeEnd = Math.round((resolvedStepValueDTO.end / thresholdValue) * 100).toInt()
        return rangeStart to rangeEnd
    }

    /**
     * Maps a StepTarget when the units are absolute (e.g., Watts 'w').
     * Since Intervals provides these as Int?, we must ensure both start and end exist.
     */
    private fun mapAbsolutePower(power: IntervalsWorkoutDocDTO.StepValueDTO?): StepTarget {
        val start = power?.start
        val end = power?.end

        return if (start != null && end != null) {
            StepTarget(start, end)
        } else {
            // Throwing exception if the expected absolute values are missing
            throw PlatformException(
                Platform.INTERVALS,
                "Absolute power values (start/end) are missing in the workout step"
            )
        }
    }
}
