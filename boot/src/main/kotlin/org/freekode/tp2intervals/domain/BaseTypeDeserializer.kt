package org.freekode.tp2intervals.domain

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException

/**
 * Custom deserializer to identify which Enum implementation matches the incoming String.
 */
class BaseTypeDeserializer : StdDeserializer<BaseType>(BaseType::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BaseType {
        val node = p.codec.readTree<JsonNode>(p)
        val value = node.asText()

        // List all your enum classes here
        val enumClasses = listOf(
            TrainingType::class.java,
            ActivityType::class.java,
            WellnessType::class.java,
            FeelingType::class.java
        )

        for (enumClass in enumClasses) {
            try {
                return java.lang.Enum.valueOf(enumClass, value) as BaseType
            } catch (e: IllegalArgumentException) {
                // Not in this enum, try the next one
            }
        }

        throw InvalidDefinitionException.from(p, "Unknown BaseType value: $value")
    }
}