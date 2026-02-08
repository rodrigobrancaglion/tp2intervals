package org.freekode.tp2intervals.dto

data class ErrorResponse(
    val platform: String?,
    val message: String,
) {
    constructor(message: String) : this(null, message)
}