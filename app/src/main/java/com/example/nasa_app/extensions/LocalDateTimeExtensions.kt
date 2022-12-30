package com.example.nasa_app.extensions

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.toLocalDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    return formatter.format(this)
}


fun LocalDateTime.getHoursAndMinutes(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    return formatter.format(this)
}

fun LocalDateTime.getDateTimeString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(this)
}

fun LocalDateTime.getDayBegging(): LocalDateTime {
    return this.toLocalDate().atStartOfDay()
}
