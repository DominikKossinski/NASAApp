package com.example.nasa_app.fragments.create_account

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.nasa_app.R
import com.example.nasa_app.api.models.ApiError
import com.example.nasa_app.architecture.BaseFragment
import com.example.nasa_app.databinding.FragmentCreateAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : BaseFragment<CreateAccountViewModel, FragmentCreateAccountBinding>() {

    override val viewModel: CreateAccountViewModel by viewModels()

    override fun handleApiError(apiError: ApiError) {
        TODO("Not yet implemented")
    }

    var nameOk = false
    var passwordOk = false
    var emailOk = false

    companion object {
        val regex = "[a-zA-Z0-9]+".toRegex()
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setUpTextWatchers() {
        binding.nameTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName(s!!)
            }

        })
        binding.passwordTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s!!)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.emailTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s!!)
            }

        })
    }

    private fun validateName(name: CharSequence) {
        if (name.toString().contentEquals("")) {
            binding.nameTextInputLayout.error = getString(R.string.empty_name)
            nameOk = false
        } else if (!regex.matches(name.toString())) {
            binding.nameTextInputLayout.error = getString(R.string.name_invalid_characters)
            nameOk = false
        } else {
            binding.nameTextInputLayout.error = null
            nameOk = true
        }
    }

    private fun validatePassword(password: CharSequence) {
        if (password.toString().contentEquals("")) {
            binding.passwordTextInputLayout.error = getString(R.string.empty_password)
            passwordOk = false
        } else if (password.length < 8) {
            binding.passwordTextInputLayout.error = getString(R.string.to_short_password)
            passwordOk = false
        } else if (!regex.matches(password.toString())) {
            binding.passwordTextInputLayout.error = getString(R.string.password_invalid_characters)
            passwordOk = false
        } else {
            binding.passwordTextInputLayout.error = null
            passwordOk = true
        }
    }

    private fun validateEmail(email: CharSequence) {
        if (email.toString().contentEquals("")) {
            binding.emailTextInputLayout.error = getString(R.string.empty_email)
            emailOk = false
        } else if (!isEmailValid(email.toString())) {
            binding.emailTextInputLayout.error = getString(R.string.not_email)
            emailOk = false
        } else {
            binding.emailTextInputLayout.error = null
            emailOk = true
        }
    }

    override fun setupOnClickListeners() {
        super.setupOnClickListeners()
        binding.backImageView.setOnClickListener {
            viewModel.navigateBack()
        }

        binding.createAccountButton.setOnClickListener {
            val name = binding.nameTextInputEditText.text.toString()
            val email = binding.emailTextInputEditText.text.toString()
            val password = binding.passwordTextInputEditText.text.toString()
            validateEmail(email)
            validateName(name)
            validatePassword(password)
            if (binding.acceptRulesCheckBox.isChecked) {
                if (nameOk && passwordOk && emailOk) {
                    binding.progressBar.visibility = View.VISIBLE
                    //TODO
//                    val user = User(0, name, password, null, email, null)
//                    CreateAccountAsyncTask(user, this).execute()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.accept_rules),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


//    TODO
//    fun showSuccessAlert(user: User) {
//        binding.progressBar.visibility = View.GONE
//        val builder = AlertDialog.Builder(this)
//        builder.setCancelable(false)
//        builder.setTitle(getString(R.string.success))
//        builder.setIcon(getDrawable(R.drawable.ic_user))
//        builder.setMessage(String.format(getString(R.string.user_created), user.name))
//        builder.setPositiveButton(
//            getString(android.R.string.ok)
//        ) { _, _ -> finish() }
//        builder.create().show()
//    }

    fun showUserExistsError() {
        binding.progressBar.visibility = View.GONE
        binding.nameTextInputLayout.error = getString(R.string.user_exists)
    }

}