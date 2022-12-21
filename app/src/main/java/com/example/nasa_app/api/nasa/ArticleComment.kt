package com.example.nasa_app.api.nasa

import java.util.*

data class ArticleComment(
    val id: Int,
    val comment: String,
    val createdAt: Date,
    val updatedAt: Date?,
    val author: User
) {
    val isEdited = updatedAt != null
}

data class ArticleCommentRequest(
    val comment: String
)