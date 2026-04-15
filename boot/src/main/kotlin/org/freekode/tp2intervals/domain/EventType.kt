package org.freekode.tp2intervals.domain

enum class EventType(val title: String) {
    RUNNING("Running"),
    CYCLING("Cycling"),
    SWIMMING("Swimming"),
    MULTISPORT("Multisport"),
    ROWING("Rowing"),
    SNOW("Snow"),
    OTHER("Other");

    val subcategories: List<SubEventType>
        get() = when (this) {
            RUNNING -> listOf(
                SubEventType.RUNNING_ROAD,
                SubEventType.RUNNING_TRAIL,
                SubEventType.RUNNING_TRACK,
                SubEventType.RUNNING_CROSS_COUNTRY,
                SubEventType.RUNNING_OTHER
            )
            CYCLING -> listOf(
                SubEventType.CYCLING_ROAD,
                SubEventType.CYCLING_MOUNTAIN,
                SubEventType.CYCLING_CYCLOCROSS,
                SubEventType.CYCLING_TRACK,
                SubEventType.CYCLING_OTHER
            )
            SWIMMING -> listOf(
                SubEventType.SWIM_OPEN_WATER,
                SubEventType.SWIM_POOL
            )
            MULTISPORT -> listOf(
                SubEventType.MULTISPORT_TRIATHLON,
                SubEventType.MULTISPORT_XTERRA,
                SubEventType.MULTISPORT_DUATHLON,
                SubEventType.MULTISPORT_AQUABIKE,
                SubEventType.MULTISPORT_AQUATHON,
                SubEventType.MULTISPORT_OTHER
            )
            ROWING -> listOf(
                SubEventType.ROWING_REGATTA,
                SubEventType.ROWING_OTHER
            )
            SNOW -> listOf(
                SubEventType.SNOW_ALPINE,
                SubEventType.SNOW_NORDIC,
                SubEventType.SNOW_SKI_MOUNTAINEERING,
                SubEventType.SNOW_SNOWSHOE,
                SubEventType.SNOW_OTHER
            )
            OTHER -> listOf(
                SubEventType.OTHER_ADVENTURE,
                SubEventType.OTHER_OBSTACLE,
                SubEventType.OTHER_SPEED_SKATE,
                SubEventType.OTHER_OTHER
            )
        }
}

enum class SubEventType(val value: String, val title: String) {
    // Running
    RUNNING_ROAD("RunningRoad", "Road Running"),
    RUNNING_TRAIL("RunningTrail", "Trail Running"),
    RUNNING_TRACK("RunningTrack", "Track Running"),
    RUNNING_CROSS_COUNTRY("RunningCrossCountry", "Cross Country"),
    RUNNING_OTHER("RunningOther", "Running"),

    // Cycling
    CYCLING_ROAD("CyclingRoad", "Road Bike"),
    CYCLING_MOUNTAIN("CyclingMountain", "Mountain Bike"),
    CYCLING_CYCLOCROSS("CyclingCyclocross", "Cyclocross"),
    CYCLING_TRACK("CyclingTrack", "Track Cycling"),
    CYCLING_OTHER("CyclingOther", "Cycling"),

    // Swimming
    SWIM_OPEN_WATER("SwimOpenWater", "Open Water Swim"),
    SWIM_POOL("SwimPool", "Lap Swim"),

    // Multisport
    MULTISPORT_TRIATHLON("MultisportTriathlon", "Triathlon"),
    MULTISPORT_XTERRA("MultisportXterra", "Xterra"),
    MULTISPORT_DUATHLON("MultisportDuathlon", "Duathlon"),
    MULTISPORT_AQUABIKE("MultisportAquabike", "Aquabike"),
    MULTISPORT_AQUATHON("MultisportAquathon", "Aquathon"),
    MULTISPORT_OTHER("MultisportOther", "Multisport"),

    // Rowing
    ROWING_REGATTA("RowingRegatta", "Regatta"),
    ROWING_OTHER("RowingOther", "Rowing"),

    // Snow
    SNOW_ALPINE("SnowAlpine", "Alpine Skiing"),
    SNOW_NORDIC("SnowNordic", "Nordic Skiing"),
    SNOW_SKI_MOUNTAINEERING("SnowSkiMountaineering", "Ski Mountaineering"),
    SNOW_SNOWSHOE("SnowSnowshoe", "Snowshoeing"),
    SNOW_OTHER("SnowOther", "Snow"),

    // Other
    OTHER_ADVENTURE("OtherAdventure", "Adventure"),
    OTHER_OBSTACLE("OtherObstacle", "OCR"),
    OTHER_SPEED_SKATE("OtherSpeedSkate", "Speed Skate"),
    OTHER_OTHER("OtherOther", "Other")
}