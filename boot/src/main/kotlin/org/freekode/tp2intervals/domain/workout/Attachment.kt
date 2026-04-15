package org.freekode.tp2intervals.domain.workout

import org.freekode.tp2intervals.integration.utils.Base64
import org.springframework.core.io.Resource

data class Attachment(
    val name: String,
    val content: String,
) {
    constructor(name: String, resource: Resource) : this(name, Base64.encodeToString(resource))

    override fun toString(): String {
        return "Attachment(name='$name')"
    }
}