package org.freekode.tp2intervals.integration.platform.rouvy.activity.dto

import java.time.LocalDate

data class RouvyActivityDTO(
    val id: String,
    val name: String,
    val cookie: String,
    val fitBytes: ByteArray? = null,
    val routeName: String? = null,
    val date: LocalDate? = null,
    val imgWorkout: ByteArray? = null
) {
    //constructor(id: String, name: String, cookie: String, fitBytes: ByteArray?, routeName: String?, date: LocalDate) : this(id, name, cookie, fitBytes, routeName, date, null)
}