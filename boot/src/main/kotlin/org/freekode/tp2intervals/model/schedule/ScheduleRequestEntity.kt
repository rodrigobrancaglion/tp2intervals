package org.freekode.tp2intervals.model.schedule

import jakarta.persistence.*
import org.freekode.tp2intervals.utils.UserContextHolder

@Table(name = "schedule_requests")
@Entity
data class ScheduleRequestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @Column
    var requestJson: String?,

    @Column
    var platform: String?,

    @Column
    var username: String?
) {
    constructor() : this(null, null, null, null)
    constructor(requestJson: String, platform: String) : this(null, requestJson, platform, UserContextHolder.username)
}