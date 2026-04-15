package org.freekode.tp2intervals.model.schedule

import jakarta.persistence.*

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
) {
    constructor() : this(null, null, null)
    constructor(requestJson: String, platform: String) : this(null, requestJson, platform)
}