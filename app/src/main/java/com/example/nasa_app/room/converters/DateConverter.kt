package com.example.nasa_app.room.converters

import androidx.room.TypeConverter
import com.example.nasa_app.extensions.toDate
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.extensions.toLocalDate
import com.example.nasa_app.extensions.toLocalDateString
import java.time.LocalDate
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

    @TypeConverter
    fun toLocalDate(string: String): LocalDate {
        return string.toLocalDate()
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toLocalDateString()
    }

}