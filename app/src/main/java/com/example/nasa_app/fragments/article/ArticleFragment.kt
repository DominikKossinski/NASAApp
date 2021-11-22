package com.example.nasa_app.fragments.article

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.nasa_app.R
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentArticleBinding
import com.example.nasa_app.extensions.toDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {

    override val viewModel: ArticleViewModel by viewModels()

    override fun setupOnClickListeners() {
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_24dp)
        binding.toolbar.inflateMenu(R.menu.menu_article)
        val menu = binding.toolbar.menu
        menu?.findItem(R.id.saveArticle)?.isVisible = false
        menu?.findItem(R.id.deleteArticle)?.isVisible = false
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.saveArticle -> {
                    viewModel.saveArticle()
                }
                R.id.deleteArticle -> {
                    viewModel.deleteArticle()
                }
            }
            false
        }
        binding.toolbar.setNavigationOnClickListener {
            viewModel.navigateBack()
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.article.collect {
                it?.let { setupArticleData(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoadingData.collect {
                binding.progressBar.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.savedArticle.collect {
                val menu = binding.toolbar.menu
                menu?.findItem(R.id.saveArticle)?.isVisible = it == null
                menu?.findItem(R.id.deleteArticle)?.isVisible = it != null
            }
        }
    }

    private fun setupArticleData(article: NasaArticle) {
        binding.toolbar.title = article.date.toDateString()
        binding.articleTextView.text = article.explanation
        binding.titleTextView.text = article.title
        binding.videoLinearLayout.isVisible = article.mediaType == NasaArticle.NasaMediaType.VIDEO
        binding.videoLinearLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            startActivity(intent)
        }
        val simpleTarget =
            object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.collapsingToolbarLayout.background = resource
                }
            }
        if (article.mediaType == NasaArticle.NasaMediaType.IMAGE) {
            Glide.with(requireContext())
                .load(article.hdurl)
                .into(simpleTarget)
        } else {
            Glide.with(requireContext())
                .load(R.mipmap.space)
                .into(simpleTarget)
        }
    }



    override fun handleApiError(apiError: ApiError) {
        Log.e("MyLog", "ApiError: $apiError")
        //TODO("Not yet implemented")
    }
}