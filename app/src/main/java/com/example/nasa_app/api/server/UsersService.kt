package com.example.nasa_app.api.server

import com.example.nasa_app.api.call.ApiResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface UsersService {

    @GET("/nasaapp-334ad/us-central1/app/api/articles")
    suspend fun getArticles(): ApiResponse<List<String>>

    @POST("/nasaapp-334ad/us-central1/app/api/articles")
    suspend fun saveArticle(@Query("date") date: String): ApiResponse<Void>

    @DELETE("/nasaapp-334ad/us-central1/app/api/articles")
    suspend fun deleteArticle(@Query("date") date: String): ApiResponse<Void>
}