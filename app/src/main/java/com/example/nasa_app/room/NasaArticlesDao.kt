package com.example.nasa_app.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasa_app.api.nasa.NasaArticle
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NasaArticlesDao {

    @Query("SELECT * FROM NasaArticle")
    suspend fun getSavedArticles(): List<NasaArticle>

    @Query("SELECT * FROM NasaArticle WHERE date = :date LIMIT 1")
    suspend fun getSavedArticleByDate(date: String): NasaArticle?

    @Query("SELECT DATE FROM NasaArticle")
    suspend fun getSavedDates(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(article: NasaArticle)

    @Delete
    suspend fun deleteArticle(article: NasaArticle)

    @Query("DELETE FROM NasaArticle WHERE date = :date")
    suspend fun deleteByDate(date: String)

}