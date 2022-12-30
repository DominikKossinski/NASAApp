package com.example.nasa_app.api.nasa

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.*

@Entity
data class NasaArticle(
    @PrimaryKey val date: LocalDate,
    @ColumnInfo(name = "explanation") val explanation: String,
    @ColumnInfo(name = "hdurl") val hdurl: String?,
    @ColumnInfo(name = "mediaType") val mediaType: NasaMediaType,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "copyright") val copyright: String?
) {
    enum class NasaMediaType {
        IMAGE,
        VIDEO
    }
}