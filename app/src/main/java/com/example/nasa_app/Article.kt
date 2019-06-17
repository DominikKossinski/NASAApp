package com.example.nasa_app

import java.util.*

data class Article(
    var title: String,
    var explanation: String,
    var date: Date,
    var mediaType: ArticleMediaType,
    var url: String,
    var drawable: ByteArray? = null,
    var saved: Boolean?
) : Comparable<Article> {
    override fun compareTo(other: Article): Int {
        if (this.date.time < other.date.time) {
            return 1
        } else if (this.date.time == other.date.time) {
            return 0
        } else {
            return -1
        }
    }

    override fun toString(): String {
        return "Article(title=$title, date=$date, mediaType=$mediaType, saved=$saved)"
    }

}