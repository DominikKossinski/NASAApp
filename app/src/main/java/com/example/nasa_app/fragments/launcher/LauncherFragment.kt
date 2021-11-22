package com.example.nasa_app.fragments.launcher

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentLauncherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@AndroidEntryPoint
class LauncherFragment : BaseFragment<LauncherViewModel, FragmentLauncherBinding>() {

    override val viewModel: LauncherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.fetchArticles()
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        lifecycleScope.launchWhenStarted {
            viewModel.activityFinish.collect {
                requireActivity().finish()
            }
        }
    }

    override fun handleApiError(apiError: ApiError) {
        when (apiError.exception) {
            is SocketTimeoutException, is UnknownHostException, is ConnectionShutdownException, is IOException -> {
                viewModel.goToMainActivity()
            }
        }
    }

}