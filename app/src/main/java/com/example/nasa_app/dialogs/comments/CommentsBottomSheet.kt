package com.example.nasa_app.dialogs.comments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
        binding.rvComments.setHasFixedSize(false)
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        adapter.currentUserId = viewModel.getUserId()
        adapter.setOnEditClickListener {
            viewModel.showEditCommentDialog(it.id, it.comment)
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        lifecycleScope.launchWhenCreated {
            viewModel.isLoadingData.collectLatest {
                binding.progressBar.isVisible = it
            }
        }
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
            viewModel.commentPostedChannel.consumeEach {
                binding.etComment.setText("")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComments()
    }
}