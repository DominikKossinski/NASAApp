package com.example.nasa_app.extensions

import android.content.Context
import com.example.nasa_app.R
import java.time.LocalDateTime
import java.util.*

fun Context.getCommentFormattedString(date: LocalDateTime): String {
    val today = LocalDateTime.now()
    val yesterday = today.minusDays(1)

    return when {
        date.getDayBegging() == today.getDayBegging() -> {
            getString(R.string.today_date_time_format, date.getHoursAndMinutes())
        }
        date.getDayBegging() == yesterday -> {
            getString(R.string.yesterday_date_time_format, date.getHoursAndMinutes())
        }
        else -> date.getDateTimeString()
    }
}