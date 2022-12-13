package com.example.nasa_app.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.parse(this) ?: throw Exception("String $this not matching format 'yyyy-MM-dd'")
}