package com.example.nasa_app.dialogs.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.architecture.BaseBottomSheet
import com.example.nasa_app.databinding.BottomSheetDailyNotificationBinding
import com.example.nasa_app.pickers.TimePicker
import com.example.nasa_app.services.DailyNotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class DailyNotificationBottomSheet :
    BaseBottomSheet<DailyNotificationViewModel, BottomSheetDailyNotificationBinding>() {

    override val viewModel: DailyNotificationViewModel by viewModels()

    override fun setOnClickListeners() {
        super.setOnClickListeners()
        binding.hourSelectView.setOnClickListener {
            showTimePicker()
        }
        binding.setButton.setOnClickListener {
            setNotification()
            viewModel.navigateBack()
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

    private fun setNotification() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.let {
            val intent = Intent(requireContext(), DailyNotificationService::class.java)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MINUTE, viewModel.minuteFlow.value)
                set(Calendar.HOUR_OF_DAY, viewModel.hourFlow.value)
            }
            Log.d("MyLog", "Setting Notification $calendar")
            val pendingIntent =
                PendingIntent.getService(
                    requireActivity(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ) //tODO request code
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}