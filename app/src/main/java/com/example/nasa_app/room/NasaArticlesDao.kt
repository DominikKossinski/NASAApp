package com.example.nasa_app.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nasa_app.api.nasa.NasaArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NasaArticlesDao {

    @Query("SELECT * FROM NasaArticle")
    fun getSavedArticles(): Flow<List<NasaArticle>>

    @Query("SELECT * FROM NasaArticle WHERE date = :date LIMIT 1")
    suspend fun getSavedArticleByDate(date: String): NasaArticle?

    @Insert
    suspend fun saveArticle(article: NasaArticle)

    @Delete
    suspend fun deleteArticle(article: NasaArticle)

}