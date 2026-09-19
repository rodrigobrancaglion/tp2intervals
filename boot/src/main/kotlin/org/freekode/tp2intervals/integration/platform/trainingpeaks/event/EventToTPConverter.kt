package org.freekode.tp2intervals.integration.platform.trainingpeaks.event

import org.freekode.tp2intervals.domain.EventType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.OtherType
import org.freekode.tp2intervals.domain.SubEventType
import org.freekode.tp2intervals.domain.event.Event
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventGoalsDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPEventResultDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.event.dto.TPGoalValueDTO

object EventToTPConverter {

    fun convert(event: Event, personId: Long?): TPEventDTO {
        val tpEventType = resolveTPEventType(event)
        val atpPriority = resolveTPPriority(event.category)

        val descriptionWithId = ExternalData.empty().withIntervals(event.externalId).buildDescription(event.description)

        return TPEventDTO(
            goals = TPEventGoalsDTO(),
            atpPriority = atpPriority,
            legs = emptyList(),
            eventDate = event.date.toString(), // YYYY-MM-DD
            name = event.name,
            personId = personId,
            eventType = tpEventType,
            distance = event.distance,
            workouts = emptyList(),
            results = listOf(
                TPEventResultDTO("Division"),
                TPEventResultDTO("Gender"),
                TPEventResultDTO("Overall")
            ),
            distanceUnits = if (event.distance != null) "Kilometers" else null,
            description = descriptionWithId,
            raceTypeDuration = resolveRaceTypeDuration(tpEventType, event),
            externalEventSource = "Intervals",
            externalEventId = event.externalId
        )
    }

    private fun resolveRaceTypeDuration(tpEventType: String, event: Event): String? {
        val durationHours = (event.durationSeconds ?: 0) / 3600.0
        val distanceKm = (event.distance ?: 0.0) / 1000.0

        val isMountain = tpEventType == SubEventType.CYCLING_MOUNTAIN.value || event.subEventType == "MTB"

        if (tpEventType.startsWith(EventType.CYCLING.title)) {
            // Priority 1: Duration
            if (event.durationSeconds != null && event.durationSeconds > 0) {
                return if (isMountain) {
                    if (durationHours > 2.0) "MtbOverTwoHours" else "MtbUnderTwoHours"
                } else {
                    if (durationHours > 2.0) "RoadBikeOverTwoHours" else "RoadBikeUnderTwoHours"
                }
            }

            // Priority 2: Distance
            if (event.distance != null && event.distance > 0) {
                if (distanceKm >= 160.0) return "BikeCentury" // 100 mi
                
                return if (isMountain) {
                    if (distanceKm >= 30.0) "MtbOverTwoHours" else "MtbUnderTwoHours"
                } else {
                    if (distanceKm >= 50.0) "RoadBikeOverTwoHours" else "RoadBikeUnderTwoHours"
                }
            }
        }

        if (tpEventType == "RunningRoad") {
            if (durationHours > 0) {
                return if (durationHours >= 2.0) "RunOverTwoHours" else "RunUnderTwoHours"
            }
        }

        return null
    }

    private fun resolveTPEventType(event: Event): String {
        // If we have a subEventType, use its TP value
        if (event.subEventType != null) {
            val subType = SubEventType.entries.find { it.value == event.subEventType }
            if (subType != null) return subType.value
        }

        // Fallback to defaults based on EventType
        return when (event.eventType) {
            EventType.RUNNING -> SubEventType.RUNNING_ROAD.value
            EventType.CYCLING -> SubEventType.CYCLING_ROAD.value
            EventType.SWIMMING -> SubEventType.SWIM_POOL.value
            EventType.MULTISPORT -> SubEventType.MULTISPORT_TRIATHLON.value
            EventType.ROWING -> SubEventType.ROWING_OTHER.value
            EventType.SNOW -> SubEventType.SNOW_OTHER.value
            else -> SubEventType.CYCLING_ROAD.value
        }
    }

    private fun resolveTPPriority(category: OtherType): String? {
        return when (category) {
            OtherType.RACE_A -> "A"
            OtherType.RACE_B -> "B"
            OtherType.RACE_C -> "C"
        }
    }

    private data class ParsedDescription(
        val description: String? = null,
        val comment: String? = null,
        val goals: List<String> = emptyList()
    )

    private fun parseDescription(fullDescription: String?): ParsedDescription {
        if (fullDescription == null) return ParsedDescription()

        val sections = fullDescription.split("### ").filter { it.isNotBlank() }
        var desc: String? = null
        var comm: String? = null
        var goalsLines: List<String> = emptyList()

        for (section in sections) {
            val lines = section.lines()
            val header = lines.firstOrNull()?.trim()
            val content = lines.drop(1).joinToString("\n").trim()
            
            // Remove the "- " prefix if present in the first line of content
            val cleanContent = content.removePrefix("- ").trim()

            when (header) {
                "Description" -> desc = cleanContent
                "Comment" -> comm = cleanContent
                "Goals" -> goalsLines = content.lines().map { it.trim() }
            }
        }

        // Fallback if no headers found
        if (desc == null && comm == null && goalsLines.isEmpty()) {
            desc = fullDescription.trim()
        }

        return ParsedDescription(desc, comm, goalsLines)
    }

    private fun buildGoals(parsed: ParsedDescription, event: Event, personId: Long?): TPEventGoalsDTO {
        var distanceKm: Double? = null
        var time: Double? = null
        var place: Int? = null
        var finish = false
        var pr = false
        val custom = mutableListOf<String>()

        for (line in parsed.goals) {
            when {
                line.startsWith("- Time:") -> {
                    val timeStr = line.removePrefix("- Time:").trim()
                    time = parseTimeToHours(timeStr)
                }
                line.startsWith("- Distance:") -> {
                    val distStr = line.removePrefix("- Distance:").trim()
                    distanceKm = distStr.split(" ").firstOrNull()?.toDoubleOrNull()
                }
                line.startsWith("- Place:") -> {
                    place = line.removePrefix("- Place:").trim().filter { it.isDigit() }.toIntOrNull()
                }
                line.startsWith("- Finish event") -> finish = true
                line.startsWith("- Set a PR") -> pr = true
                line.startsWith("- Custom:") -> { /* following lines are custom */ }
                line.startsWith("- ") || line.startsWith(" - ") -> {
                    // Check if we are in custom section
                    custom.add(line.trimStart('-', ' ').trim())
                }
            }
        }

        // If no goals found in description, use values from Event object
        if (time == null && event.durationSeconds != null) {
            time = event.durationSeconds.toDouble() / 3600.0
        }
        if (distanceKm == null && event.distance != null) {
            distanceKm = event.distance / 1000.0
        }

        return TPEventGoalsDTO(
            athleteEventId = null,
            athleteId = personId,
            distance = distanceKm?.let { TPGoalValueDTO(it, displayUnits = "Kilometers", complete = false, sortOrder = 0) },
            time = time?.let { TPGoalValueDTO(it, displayUnits = "Hours", complete = false, sortOrder = 0) },
            place = place?.let { TPGoalValueDTO(it, complete = false, sortOrder = 0) },
            finish = if (finish) TPGoalValueDTO(true, complete = false, sortOrder = 0) else null,
            pr = if (pr) TPGoalValueDTO(true, complete = false, sortOrder = 0) else null,
            written = custom.mapIndexed { index, s -> TPGoalValueDTO(s, complete = false, sortOrder = index) }
        )
    }

    private fun parseTimeToHours(timeStr: String): Double? {
        val parts = timeStr.split(":").mapNotNull { it.toIntOrNull() }
        return when (parts.size) {
            3 -> parts[0] + parts[1] / 60.0 + parts[2] / 3600.0
            2 -> parts[0] / 60.0 + parts[1] / 3600.0
            else -> null
        }
    }
}
