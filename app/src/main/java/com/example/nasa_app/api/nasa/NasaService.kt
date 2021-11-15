package com.example.nasa_app.api.nasa

import com.example.nasa_app.api.call.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaService {

    @GET("/planetary/apod")
    suspend fun getArticle(
        @Query("api_key") apikey: String,
        @Query("date") date: String
    ): ApiResponse<NasaArticle>

    @GET("planetary/apod")
    suspend fun getArticles(
        @Query("api_key") apikey: String,
        @Query("start_date") start_date: String,
        @Query("end_date") endDate: String
    ): ApiResponse<List<NasaArticle>>
}