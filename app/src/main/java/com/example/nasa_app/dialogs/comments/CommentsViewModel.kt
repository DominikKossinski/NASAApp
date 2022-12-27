package com.example.nasa_app.dialogs.comments

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.ArticleComment
import com.example.nasa_app.api.nasa.ArticleCommentRequest
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val articlesService: ArticlesService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    private val date = savedStateHandle.get<String>("date")!!

    private val commentFlow = MutableStateFlow("")
    val commentsFlow = MutableStateFlow<List<ArticleComment>?>(null)
    val commentPostedChannel = Channel<Unit>()
    val isCommentButtonEnabled = combine(isLoadingData, commentFlow) { isLoading, comment ->
        return@combine !isLoading && comment.isNotBlank()
    }

    init {
        fetchComments()
    }

    fun fetchComments() {
        makeRequest {
            val comments = articlesService.getArticleComments(date)
            commentsFlow.value = comments
        }
    }

    fun setComment(comment: String) {
        commentFlow.value = comment
    }

    fun postComment() {
        makeRequest {
            Log.d("MyLog", "PostComment: ${commentFlow.value}")
            articlesService.postComment(date, ArticleCommentRequest(commentFlow.value))
            commentFlow.value = ""
            commentPostedChannel.send(Unit)
            setToastMessage(R.string.comments_comment_posted)
            fetchComments()
        }
    }

    fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
