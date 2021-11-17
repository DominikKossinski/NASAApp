package com.example.nasa_app.fragments.login

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.example.nasa_app.R
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentLoginBinding
import com.example.nasa_app.extensions.doOnTextChanged
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    override val viewModel: LoginViewModel by viewModels()

    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.loginButton.setOnClickListener {
            val name = binding.nameTextInputEditText.text.toString()
            val password = binding.passwordTextInputEditText.text.toString()
            if (name.compareTo("") == 0) {
                binding.nameTextInputLayout.error = getString(R.string.empty_name)
            }
            if (password.contentEquals("")) {
                binding.passwordTextInputLayout.error = getString(R.string.empty_password)
            }
            if (!password.equals("") and !name.equals("")) {
                binding.progressBar.visibility = View.VISIBLE
                val gson = GsonBuilder().create()
//           TODO     val user = gson.toJson(User(0, name, password, null, "", null))
//                LoginAsyncTask(
//                    this, user, binding.nameTextInputLayout, binding.passwordTextInputLayout,
//                    binding.nameTextInputEditText, binding.passwordTextInputEditText, binding.progressBar
//                ).execute()
            }
        }
        binding.createAccountButton.setOnClickListener {
            viewModel.navigateToCreateAccount()
        }
        setUpTextWatchers()
    }

    private fun setUpTextWatchers() {
        //TODO refactor
        binding.nameTextInputEditText.doOnTextChanged {
            if (it.compareTo("") == 0) {
                if (!binding.nameTextInputLayout.error.toString().contentEquals(getString(R.string.no_user))) {
                    binding.nameTextInputLayout.error = getString(R.string.empty_name)
                }
            } else {
                binding.nameTextInputLayout.error = null
            }
        }
        binding.passwordTextInputEditText.doOnTextChanged {
            if (it.compareTo("") == 0) {
                if (!binding.passwordTextInputLayout.error.toString().contentEquals(getString(R.string.wrong_password))) {
                    binding.passwordTextInputLayout.error = getString(R.string.empty_password)
                }
            } else {
                binding.passwordTextInputLayout.error = null
            }
        }
    }


    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }
}