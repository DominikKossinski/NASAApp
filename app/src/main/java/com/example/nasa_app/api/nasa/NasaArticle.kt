package com.example.nasa_app.api.nasa

import com.google.gson.annotations.SerializedName
import java.util.*

data class NasaArticle(
    val copyright : String,
    val date: Date,
    val explanation: String,
    val hdurl: String,
    @SerializedName("media_type")
    val mediaType: NasaMediaType,
    val title: String,
    val url: String
) {
    enum class NasaMediaType {
        @SerializedName("image")
        IMAGE,
        @SerializedName("video")
        VIDEO
    }
}