package com.example.nasa_app.dialogs.comments.edit

import androidx.fragment.app.viewModels
import com.example.nasa_app.architecture.BaseDialog
import com.example.nasa_app.databinding.DialogEditCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCommentDialog : BaseDialog<EditCommentViewModel, DialogEditCommentBinding>() {
    override val viewModel: EditCommentViewModel by viewModels()
}