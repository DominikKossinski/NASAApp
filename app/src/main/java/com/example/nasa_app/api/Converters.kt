package com.example.nasa_app.api

import com.example.nasa_app.extensions.toDate
import com.example.nasa_app.extensions.toDateString
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class Converters : JsonDeserializer<Date>, JsonSerializer<Date> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        return json!!.asString.toDate()
    }

    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toDateString())
    }
}