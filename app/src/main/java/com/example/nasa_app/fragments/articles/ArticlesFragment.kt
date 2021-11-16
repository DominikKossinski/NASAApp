package com.example.nasa_app.fragments.articles

import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.DBHelper
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.activities.MainActivity
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentArticlesBinding
import com.example.nasa_app.extensions.toDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map


@AndroidEntryPoint
class ArticlesFragment : BaseFragment<ArticlesViewModel, FragmentArticlesBinding>() {

    override val viewModel: ArticlesViewModel by viewModels()

    private val adapter = ArticlesPagingAdapter()

    var user: User? = null

    override fun setupOnClickListeners() {
        (requireActivity() as? MainActivity)?.setupDrawer(binding.toolbar)
        binding.toolbar.inflateMenu(R.menu.menu_articles)
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }

        })
        binding.articlesSwipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
        binding.fab.setOnClickListener {
            showArticleAddDialog()
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter.setOnItemClickListener { article ->
            viewModel.navigateToArticle(article.date.toDateString())
        }
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.articlesRecyclerView.adapter = adapter
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect {
                if (it.refresh != LoadState.Loading) {
                    binding.noArticlesTextView.isVisible = adapter.itemCount == 0
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.articles.collectLatest {
                adapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoadingData.collect {
                binding.articlesSwipeRefreshLayout.isRefreshing = it
            }
        }
    }

    fun getLastArticles(
        dbHelper: DBHelper
    ) {
        getDataFromDB(false)
//        adapter!!.articles = articles
        val missingDates = dbHelper.getMissingLastArticles(user!!)
        //TODO missing dates
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:LastArticlesFrag", "Missing Dates Size: ${missingDates.size}")
            Log.d("MyLog:LastArticlesFrag", "Missing Dates: $missingDates")
        }
        binding.articlesSwipeRefreshLayout.isRefreshing = missingDates.size != 0
    }


    private fun getDataFromDB(end: Boolean) {
        binding.articlesSwipeRefreshLayout.isRefreshing = true
        val dbHelper = DBHelper(requireActivity())
        //TODO
//        articles = dbHelper.getAllArticles(user!!)
//        adapter!!.articles = articles
//        adapter!!.sortNotify()
        binding.articlesSwipeRefreshLayout.isRefreshing = !end
    }

    private fun showArticleAddDialog() {
        viewModel.showArticleAddDialog()
    }

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }


}
