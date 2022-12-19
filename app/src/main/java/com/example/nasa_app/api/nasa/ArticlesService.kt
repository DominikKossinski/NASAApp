package com.example.nasa_app.api.nasa

import com.example.nasa_app.api.call.ApiResponse
import retrofit2.http.*

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

    @DELETE("/api/saved-articles/{date}")
    suspend fun deleteSavedArticle(@Path("date") date: String)

    @GET("/api/articles/{date}/comments")
    suspend fun getArticleComments(@Path("date") date: String): List<ArticleComment>

    @POST("/api/articles/{date}/comments")
    suspend fun postComment(@Path("date") date: String)


    @PUT("/api/articles/{date}/comments/{commentId}")
    suspend fun putComment(@Path("date") date: String, @Path("commentId") commentId: String)

}

