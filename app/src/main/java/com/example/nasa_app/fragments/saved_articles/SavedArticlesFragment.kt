package com.example.nasa_app.fragments.saved_articles

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nasa_app.activities.main.MainActivity
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentSavedArticlesBinding
import com.example.nasa_app.extensions.toDateString
import com.example.nasa_app.extensions.toLocalDateString
import com.example.nasa_app.fragments.saved_articles.adapter.ArticlesRVAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedArticlesFragment : BaseFragment<SavedArticlesViewModel, FragmentSavedArticlesBinding>() {

    override val viewModel: SavedArticlesViewModel by viewModels()

    private val adapter = ArticlesRVAdapter()


    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.articlesSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshArticles()
        }
        (requireActivity() as? MainActivity)?.setupDrawer(binding.toolbar)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter.setOnItemClickListener {
            viewModel.navigateToSavedArticle(it.date.toLocalDateString())
        }
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRecyclerView.adapter = adapter
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.savedArticles.collect {
                adapter.items.clear()
                adapter.items.addAll(it)
                binding.noArticlesTextView.isVisible = it.isEmpty()
                adapter.notifyDataSetChanged()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoadingData.collect {
                binding.articlesSwipeRefreshLayout.isRefreshing = it
            }
        }
    }

    override fun handleApiError(apiError: ApiError) {}

    override fun onResume() {
        super.onResume()
        viewModel.getArticles()
    }
}