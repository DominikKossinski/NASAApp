package com.example.nasa_app.fragments.create_account

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.nasa_app.R
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentCreateAccountBinding
import com.example.nasa_app.extensions.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CreateAccountFragment : BaseFragment<CreateAccountViewModel, FragmentCreateAccountBinding>() {

    override val viewModel: CreateAccountViewModel by viewModels()

    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.loginButton.setOnClickListener {
            viewModel.navigateBack()
        }
        binding.createAccountButton.setOnClickListener {
            viewModel.createAccount()
        }
        binding.emailTie.doOnTextChanged { text ->
            viewModel.setEmail(text)
        }
        binding.passwordTie.doOnTextChanged { text ->
            viewModel.setPassword(text)
        }
        binding.confirmPasswordTie.doOnTextChanged { text ->
            viewModel.setConfirmPassword(text)
        }
        binding.regulationsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRegulationsAccepted(isChecked)
        }
    }

    override fun collectFlow() {
        super.collectFlow()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isCreateAccountButtonEnabled.collect {
                binding.createAccountButton.isEnabled = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.emailError.collect {
                binding.emailTil.error = it?.let {
                    binding.emailTie.setText("")
                    binding.passwordTie.setText("")
                    binding.confirmPasswordTie.setText("")
                    getString(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.passwordError.collect {
                binding.passwordTil.error = it?.let {
                    binding.passwordTie.setText("")
                    binding.confirmPasswordTie.setText("")
                    getString(it)
                }
            }
        }
    }

    override fun handleApiError(apiError: ApiError) {    }

}