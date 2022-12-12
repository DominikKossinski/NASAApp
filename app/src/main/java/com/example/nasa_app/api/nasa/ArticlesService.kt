package com.example.nasa_app.api.nasa

import com.example.nasa_app.api.call.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesService {

    @GET("/api/articles/{date}")
    suspend fun getArticle(
        @Path("date") date: String
    ): ApiResponse<NasaArticle>

    @GET("/api/articles/")
    suspend fun getArticles(
        @Query("from") from: String,
        @Query("to") to: String
    ): ApiResponse<List<NasaArticle>>

    @GET("/api/saved-articles")
    suspend fun getSavedArticles(): ApiResponse<List<NasaArticle>>

    @POST("/api/saved-articles")
    suspend fun postSavedArticle(@Body saveArticleRequest: SaveArticleRequest)
}