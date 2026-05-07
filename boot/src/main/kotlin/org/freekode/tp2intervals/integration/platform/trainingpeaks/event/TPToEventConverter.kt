package org.freekode.tp2intervals.integration.platform.trainingpeaks.event

import org.freekode.tp2intervals.domain.EventType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.OtherType
import org.freekode.tp2intervals.domain.SubEventType
import org.freekode.tp2intervals.domain.event.Event
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventGoalsDTO
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Converts a TP event DTO to the domain Event model.
 * Separated from the repository to keep conversion logic isolated and testable.
 *
 * TP's atpPriority field ("A", "B", "C") maps directly to ICU's OtherType (RACE_A/B/C).
 * The externalId is the TP event ID, used downstream for deduplication.
 */
object TPToEventConverter {

    fun convert(dto: TPEventDTO): Event? {
        val externalData = ExternalData.empty()
            .withTrainingPeaks(dto.id?.toString())
            .fromSimpleString(dto.description ?: "")

        val icuId = externalData.intervalsId ?: dto.id?.toString() ?: return null

        val date = dto.eventDate?.let {
            if (it.contains("T")) {
                LocalDateTime.parse(it).toLocalDate()
            } else {
                LocalDate.parse(it)
            }
        } ?: return null
        val eventName = dto.name?.takeIf { it.isNotBlank() } ?: "Event ${dto.id}"
        val sport = resolveEventType(dto.eventType)
        val subSport = resolveSubEventType(dto.eventType)

        // Try to get duration/distance from goals first
        var durationSec = dto.goals?.time?.value?.let { (it * 3600).toLong() }
        var distance = dto.goals?.distance?.value ?: dto.distance

        // Apply defaults based on raceTypeDuration if both are missing
        if (durationSec == null && distance == null) {
            when (dto.raceTypeDuration) {
                "RoadBikeUnderTwoHours", "MtbUnderTwoHours" -> durationSec = 3600
                "RoadBikeOverTwoHours", "MtbOverTwoHours" -> durationSec = 7200
                "BikeCentury" -> distance = 100000.0
            }
        }

        // Map TP's atpPriority (A/B/C) to ICU's OtherType (RACE_A/B/C)
        val category = resolveRacePriority(dto.atpPriority)
        return Event(
            externalId = icuId,
            name = eventName,
            date = date,
            category = category,
            eventType = sport,
            subEventType = subSport?.value,
            description = buildFullDescription(dto.description, dto.comment, dto.goals),
            durationSeconds = durationSec,
            distance = distance,
            tss = null,
        )
    }

    private fun buildFullDescription(description: String?, comment: String?, goals: TPEventGoalsDTO?): String? {
        val baseDescription = description?.takeIf { it.isNotBlank() }
        val commentPart = comment?.takeIf { it.isNotBlank() }

        val sections = mutableListOf<String>()

        if (baseDescription != null) {
            sections.add("### Description\n- $baseDescription")
        }

        if (commentPart != null) {
            sections.add("### Comment\n- $commentPart")
        }

        val mainText = sections.joinToString("\n\n")

        if (goals == null) return mainText.takeIf { it.isNotBlank() }

        val goalList = mutableListOf<String>()

        goals.time?.value?.let { hours ->
            val totalSeconds = (hours * 3600).toInt()
            val h = totalSeconds / 3600
            val m = (totalSeconds % 3600) / 60
            val s = totalSeconds % 60
            goalList.add("- Time: ${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}")
        }

        goals.distance?.value?.let { meters ->
            val km = (meters / 1000).toInt()
            goalList.add("- Distance: $km km")
        }

        goals.place?.value?.let { p ->
            val suffix = when {
                p % 100 in 11..13 -> "th"
                p % 10 == 1 -> "st"
                p % 10 == 2 -> "nd"
                p % 10 == 3 -> "rd"
                else -> "th"
            }
            goalList.add("- Place: $p$suffix")
        }

        if (goals.finish?.value == true) {
            goalList.add("- Finish event")
        }

        if (goals.pr?.value == true) {
            goalList.add("- Set a PR")
        }

        goals.written?.mapNotNull { it.value }?.forEachIndexed { index, value ->
            if (index == 0) {
                goalList.add("- Custom:")
                goalList.add(" - $value")
            } else {
                goalList.add(" - $value")
            }
        }

        if (goalList.isEmpty()) return description

        val goalsHeader = "### Goals"
        val goalsBody = goalList.joinToString("\n")

        return if (mainText.isBlank()) {
            "$goalsHeader\n$goalsBody"
        } else {
            "$mainText\n\n$goalsHeader\n$goalsBody"
        }
    }

    private fun resolveEventType(eventTypeStr: String?): EventType? {
        val subType = resolveSubEventType(eventTypeStr) ?: return null
        return EventType.entries.find { it.subcategories.contains(subType) }
    }

    private fun resolveSubEventType(eventTypeStr: String?): SubEventType? {
        return SubEventType.entries.find { it.value == eventTypeStr }
    }

    /**
     * Maps TP's atpPriority field ("A", "B", "C") to ICU's OtherType.
     * Defaults to RACE_A if not present.
     */
    private fun resolveRacePriority(atpPriority: String?): OtherType = when (atpPriority?.uppercase()) {
        "A" -> OtherType.RACE_A
        "B" -> OtherType.RACE_B
        "C" -> OtherType.RACE_C
        else -> OtherType.RACE_A  // Default if missing or unrecognized
    }
}
