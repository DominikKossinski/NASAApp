package com.example.nasa_app.dialogs.notifications

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.architecture.BaseBottomSheet
import com.example.nasa_app.databinding.BottomSheetDailyNotificationBinding
import com.example.nasa_app.fragments.settings.SettingsFragment
import com.example.nasa_app.pickers.TimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DailyNotificationBottomSheet :
    BaseBottomSheet<DailyNotificationViewModel, BottomSheetDailyNotificationBinding>() {

    override val viewModel: DailyNotificationViewModel by viewModels()


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult(SettingsFragment.REFRESH, Bundle())
    }

    override fun setOnClickListeners() {
        super.setOnClickListeners()
        binding.hourSelectView.setOnClickListener {
            showTimePicker()
        }
        binding.setButton.setOnClickListener {
            viewModel.setupDailyNotification()
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.hourFlow.collectLatest {
                binding.hourSelectView.hour = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.minuteFlow.collectLatest {
                binding.hourSelectView.minute = it
            }
        }
    }

    private fun showTimePicker() {
        TimePicker(
            Pair(viewModel.hourFlow.value, viewModel.minuteFlow.value)
        ) { hourOfDay, minute ->
            viewModel.hourFlow.value = hourOfDay
            viewModel.minuteFlow.value = minute
        }.show(childFragmentManager, TimePicker.TAG)
    }
}