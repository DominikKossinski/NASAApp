package com.example.nasa_app

import java.util.*

data class Article(
    var title: String, var explanation: String, var date: Date, var mediaType: ArticleMediaType, var url: String,
    var hdUrl: String?, var drawable: ByteArray? = null
)