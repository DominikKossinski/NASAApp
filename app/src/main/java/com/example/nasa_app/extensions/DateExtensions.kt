package com.example.nasa_app.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toDateString(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(this)
}

fun Date.getDayBegging(): Date {
    return Date(this.time - (this.time % (24 * 60 * 60 * 1_000)))
}

fun Date.minusDays(days: Long): Date {
    return Date(this.time - (days * 24 * 60 * 60 * 1_000))
}