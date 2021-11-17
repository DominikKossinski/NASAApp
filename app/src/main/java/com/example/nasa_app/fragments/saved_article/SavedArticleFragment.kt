package com.example.nasa_app.fragments.saved_article

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.example.nasa_app.databinding.FragmentSavedArticleBinding
import com.example.nasa_app.extensions.toDateString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SavedArticleFragment : BaseFragment<SavedArticleViewModel, FragmentSavedArticleBinding>() {

    override val viewModel: SavedArticleViewModel by viewModels()

    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_24dp)
        binding.toolbar.inflateMenu(R.menu.menu_article)
        val menu = binding.toolbar.menu
        menu?.findItem(R.id.saveArticle)?.isVisible = false
        menu?.findItem(R.id.deleteArticle)?.isVisible = true
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
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
            viewModel.savedArticle.collectLatest {
                it?.let { setupArticleData(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoadingData.collectLatest {
                binding.progressBar.isVisible = it
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
        if (article.mediaType == NasaArticle.NasaMediaType.IMAGE) {
            val simpleTarget =
                object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.collapsingToolbarLayout.background = resource
                    }
                }
            Glide.with(requireContext())
                .load(article.hdurl)
                .into(simpleTarget)
        }
    }

    override fun handleApiError(apiError: ApiError) {

    }
}