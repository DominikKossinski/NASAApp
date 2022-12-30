package com.example.nasa_app.api

import com.example.nasa_app.extensions.toLocalDate
import com.example.nasa_app.extensions.toLocalDateString
import com.example.nasa_app.extensions.toLocalDateTime
import com.example.nasa_app.extensions.toLocalDateTimeString
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateConverter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        return json!!.asString.toLocalDate()
    }

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toLocalDateString())
    }

}

class LocalDateTimeConverter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return json!!.asString.toLocalDateTime()
    }

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toLocalDateTimeString())
    }


}