package com.example.nasa_app.api.nasa

import java.time.LocalDateTime

data class ArticleComment(
    val id: Int,
    val comment: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val author: User
) {
    val isEdited = updatedAt != null
}

data class ArticleCommentRequest(
    val comment: String
)