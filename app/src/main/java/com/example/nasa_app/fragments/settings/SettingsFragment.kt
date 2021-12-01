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
        binding.notificationsCs.setOnChangeClickListener {
            //TODO
        }
    }

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }
}