package com.example.nasa_app.fragments.login

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentLoginBinding
import com.example.nasa_app.extensions.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    override val viewModel: LoginViewModel by viewModels()

    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }
        binding.createAccountButton.setOnClickListener {
            viewModel.navigateToCreateAccount()
        }
        binding.emailTie.doOnTextChanged { text ->
            viewModel.setEmail(text)
        }
        binding.passwordTie.doOnTextChanged { text ->
            viewModel.setPassword(text)
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoadingData.collect {
                binding.progressBar.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoginButtonEnabled.collect {
                binding.loginButton.isEnabled = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.emailError.collect {
                binding.emailTil.error = it?.let {
                    binding.emailTie.setText("")
                    binding.passwordTie.setText("")
                    getString(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.passwordError.collect {
                binding.passwordTil.error = it?.let {
                    binding.passwordTie.setText("")
                    getString(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.activityFinish.collect {
                requireActivity().finish()
            }
        }
    }

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }
}