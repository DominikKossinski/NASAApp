package com.example.nasa_app.fragments.email_resend

import androidx.fragment.app.viewModels
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentEmailResendBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailResendFragment: BaseFragment<EmailResendViewModel, FragmentEmailResendBinding>() {

    override val viewModel: EmailResendViewModel by viewModels()

    override fun setupOnClickListeners() {
        binding.resendButton.setOnClickListener {
            viewModel.resendEmail()
        }
        binding.loginButton.setOnClickListener {
            viewModel.navigateToLogin()
        }
    }

    override fun handleApiError(apiError: ApiError) {    }
}