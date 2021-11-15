package com.example.nasa_app.api.models

data class ApiErrorBody(
    val message: String,
    val description: String
)

data class ApiError(
    val code: Int,
    val body: ApiErrorBody?
)
