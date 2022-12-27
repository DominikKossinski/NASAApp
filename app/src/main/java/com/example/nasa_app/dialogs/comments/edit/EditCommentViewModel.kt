package com.example.nasa_app.dialogs.comments.edit

import androidx.lifecycle.SavedStateHandle
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.ArticleCommentRequest
import com.example.nasa_app.api.nasa.ArticlesService
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EditCommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val articlesService: ArticlesService,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) :
    BaseViewModel(preferencesHelper, appDatabase) {

    private val args = EditCommentDialogArgs.fromSavedStateHandle(savedStateHandle)

    private val commentFlow = MutableStateFlow(args.comment)
    val isSaveButtonEnabled = commentFlow.map {
        it.isNotBlank() && it != args.comment
    }

    fun getArgsComment() = args.comment

    fun setComment(comment: String) {
        commentFlow.value = comment
    }

    fun saveComment() {
        makeRequest {
            articlesService.putComment(
                args.date,
                args.commentId,
                ArticleCommentRequest(commentFlow.value)
            )
            setToastMessage(R.string.comments_comment_edited)
            navigateBack()
        }
    }

}
