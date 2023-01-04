package com.example.nasa_app.dialogs.comments.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.architecture.BaseDialog
import com.example.nasa_app.databinding.DialogEditCommentBinding
import com.example.nasa_app.extensions.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditCommentDialog : BaseDialog<EditCommentViewModel, DialogEditCommentBinding>() {
    override val viewModel: EditCommentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.etvComment.setText(viewModel.getArgsComment())
        binding.etvComment.doOnTextChanged {
            viewModel.setComment(it)
        }
        binding.btnCancel.setOnClickListener {
            viewModel.navigateBack()
        }
        binding.btnSave.setOnClickListener {
            viewModel.saveComment()
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        lifecycleScope.launchWhenCreated {
            viewModel.isSaveButtonEnabled.collectLatest {
                binding.btnSave.isEnabled = it
            }
        }
    }
}