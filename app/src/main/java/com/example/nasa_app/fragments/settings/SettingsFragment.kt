package com.example.nasa_app.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.R
import com.example.nasa_app.activities.main.MainActivity
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListeners()
    }
    override fun setupOnClickListeners() {
        (requireActivity() as? MainActivity)?.setupDrawer(binding.toolbar)
        //TODO setup initial state of notifications
        binding.dailyNotificationTwcv.setOnChangeClickListener {
            viewModel.showDailyNotificationBottomSheet()
        }
        binding.notificationsCs.setOnChangeClickListener {
            if (it) {
                viewModel.showDailyNotificationBottomSheet()
            } else {
                viewModel.cancelDailyNotifications()
            }
            viewModel.refresh()
        }
    }

    private fun setFragmentResultListeners() {
        parentFragmentManager.setFragmentResultListener(REFRESH, viewLifecycleOwner) { _, _ ->
            viewModel.refresh()
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.dailyNotificationTimeFlow.collectLatest {
                binding.notificationsCs.setChecked(it != null)
                binding.dailyNotificationTwcv.isVisible = it != null
                it?.let {
                    binding.dailyNotificationTwcv.text =
                        getString(R.string.current_daily_notification, it.first, it.second)
                }
            }
        }
    }

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }

    companion object {
        const val REFRESH = "REFRESH"
    }
}