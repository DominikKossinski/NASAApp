package com.example.nasa_app.utils.analitics

import android.os.Bundle
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.extensions.toLocalDateString
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsTracker {

    private val analytics = Firebase.analytics

    fun setUserId(userId: String?) {
        analytics.setUserId(userId)
    }

    fun logClickOpenArticle(article: NasaArticle) {
        val params = Bundle().apply {
            putString("date", article.date.toLocalDateString().take(MAX_PARAMETER_LENGTH))
            putString("title", article.title.take(MAX_PARAMETER_LENGTH))
            putString("media_type", article.mediaType.name.take(MAX_PARAMETER_LENGTH))
        }
        logEvent(AnalyticsEvent.CLICK_OPEN_ARTICLE, params)
    }

    private fun logEvent(event: AnalyticsEvent, params: Bundle?) {
        analytics.logEvent(event.eventName, params)
    }

    private enum class AnalyticsEvent(val eventName: String) {
        CLICK_OPEN_ARTICLE("click_open_article") // Params: date, title, media_type
    }

    companion object {
        private const val MAX_PARAMETER_LENGTH = 100
    }
}