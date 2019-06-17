package com.example.nasa_app

data class User(
    var id: Long,
    var name: String,
    var password: String?,
    var role: String?,
    var email: String?,
    var apiKey: String?
)