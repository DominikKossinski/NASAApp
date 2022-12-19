package com.example.nasa_app.dialogs.comments

import androidx.fragment.app.viewModels
import com.example.nasa_app.architecture.BaseBottomSheet
import com.example.nasa_app.databinding.DialogCommentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsBottomSheet : BaseBottomSheet<CommentsViewModel, DialogCommentsBinding>() {

    override val viewModel: CommentsViewModel by viewModels()
}