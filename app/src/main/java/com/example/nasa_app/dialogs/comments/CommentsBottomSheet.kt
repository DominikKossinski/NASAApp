package com.example.nasa_app.dialogs.comments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nasa_app.architecture.BaseBottomSheet
import com.example.nasa_app.databinding.DialogCommentsBinding
import com.example.nasa_app.extensions.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CommentsBottomSheet : BaseBottomSheet<CommentsViewModel, DialogCommentsBinding>() {

    override val viewModel: CommentsViewModel by viewModels()

    private val adapter = CommentsRvAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.etComment.doOnTextChanged {
            viewModel.setComment(it)
        }
        binding.btnComment.setOnClickListener {
            viewModel.postComment()
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvComments.adapter = adapter
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun collectFlow() {
        super.collectFlow()
        lifecycleScope.launchWhenCreated {
            viewModel.isCommentButtonEnabled.collectLatest {
                binding.btnComment.isEnabled = it
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.commentsFlow.collectLatest {
                it?.let {
                    adapter.items.clear()
                    adapter.items.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.clearCommentChannel.consumeEach {
                binding.etComment.setText("")
            }
        }
        // TODO progress bar
    }
}