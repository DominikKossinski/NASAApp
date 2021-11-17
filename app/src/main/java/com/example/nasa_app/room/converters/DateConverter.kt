package com.example.nasa_app.room.converters

import androidx.room.TypeConverter
import com.example.nasa_app.extensions.toDate
import com.example.nasa_app.extensions.toDateString
import java.util.*

class DateConverter {

    @TypeConverter
    fun toDate(string: String): Date {
        return string.toDate()
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return date.toDateString()
    }
}