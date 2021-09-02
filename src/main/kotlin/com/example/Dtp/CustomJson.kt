package com.example.Dtp

import com.example.Dtp.model.Registrator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class CustomJson : JsonSerializer<Registrator>() {
    override fun serialize(
        value: Registrator,
        jsonGenerator: JsonGenerator,
        serializerProvider: SerializerProvider
    ) {
        jsonGenerator.writeStartObject()
        jsonGenerator.writeNumberField("id", value.id)
        jsonGenerator.writeStringField("code", value.code)
        jsonGenerator.writeStringField("location", "POINT ${value.location.x} ${value.location.y}")
        jsonGenerator.writeEndObject()
    }
}


