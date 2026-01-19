package org.freekode.tp2intervals.domain

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = BaseTypeDeserializer::class)
interface BaseType {
    val title: String
    val category: CategoryType
}