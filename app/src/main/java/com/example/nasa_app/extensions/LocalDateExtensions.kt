package com.example.nasa_app.extensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDate.toLocalDateString(): String {
    val f = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    return f.format(this)
}
