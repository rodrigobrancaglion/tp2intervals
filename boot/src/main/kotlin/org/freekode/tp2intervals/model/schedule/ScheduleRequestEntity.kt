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
) {
    constructor() : this(null, null)
    constructor(requestJson: String) : this(null, requestJson)
}