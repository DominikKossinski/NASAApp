package com.example.nasa_app.api.call

data class ApiResponse<out T: Any>(val body: T?)