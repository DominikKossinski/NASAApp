package com.example.nasa_app.fragments.settings

import androidx.fragment.app.viewModels
import com.example.nasa_app.activities.main.MainActivity
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override val viewModel: SettingsViewModel by viewModels()

    override fun setupOnClickListeners() {
        (requireActivity() as? MainActivity)?.setupDrawer(binding.toolbar)
        //TODO setup initial state of notifications
        binding.notificationsCs.setOnChangeClickListener {
            if (it) {
                viewModel.showDailyNotificationBottomSheet()
            } // TODO else {
                //TODO clear daily notification
            //TODO }
        }
    }

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }
}