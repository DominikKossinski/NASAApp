package com.example.nasa_app.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.room.converters.DateConverter
import com.example.nasa_app.room.converters.MediaTypeConverter

@Database(entities = [NasaArticle::class], version = 1)
@TypeConverters(DateConverter::class, MediaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun nasaArticlesDao(): NasaArticlesDao
}