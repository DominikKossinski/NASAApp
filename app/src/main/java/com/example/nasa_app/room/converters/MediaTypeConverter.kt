package com.example.nasa_app.room.converters

import androidx.room.TypeConverter
import com.example.nasa_app.api.nasa.NasaArticle

class MediaTypeConverter {

    @TypeConverter
    fun toMediaType(string: String): NasaArticle.NasaMediaType {
        return NasaArticle.NasaMediaType.valueOf(string)
    }

    @TypeConverter
    fun fromMediaType(mediaType: NasaArticle.NasaMediaType): String {
        return mediaType.name
    }
}