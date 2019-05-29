package com.example.nasa_app.activities

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.asynctasks.CreateAccountAsyncTask
import kotlinx.android.synthetic.main.activity_create_account.*


class CreateAccountActivity : AppCompatActivity() {

    var nameOk = false
    var passwordOk = false
    var emailOk = false

    companion object {
        val regex = "[a-zA-Z0-9]+".toRegex()
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        setUpOnClickListeners()
        setUpTextWatchers()
    }

    private fun setUpTextWatchers() {
        nameTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName(s!!)
            }

        })
        passwordTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s!!)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        emailTextInputEditText.addTextChangedListener(object : TextWatcher {
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
            nameTextInputLayout.error = getString(R.string.empty_name)
            nameOk = false
        } else if (!regex.matches(name.toString())) {
            nameTextInputLayout.error = getString(R.string.name_invalid_characters)
            nameOk = false
        } else {
            nameTextInputLayout.error = null
            nameOk = true
        }
    }

    private fun validatePassword(password: CharSequence) {
        if (password.toString().contentEquals("")) {
            passwordTextInputLayout.error = getString(R.string.empty_password)
            passwordOk = false
        } else if (password.length < 8) {
            passwordTextInputLayout.error = getString(R.string.to_short_password)
            passwordOk = false
        } else if (!regex.matches(password.toString())) {
            passwordTextInputLayout.error = getString(R.string.password_invalid_characters)
            passwordOk = false
        } else {
            passwordTextInputLayout.error = null
            passwordOk = true
        }
    }

    private fun validateEmail(email: CharSequence) {
        if (email.toString().contentEquals("")) {
            emailTextInputLayout.error = getString(R.string.empty_email)
            emailOk = false
        } else if (!isEmailValid(email.toString())) {
            emailTextInputLayout.error = getString(R.string.not_email)
            emailOk = false
        } else {
            emailTextInputLayout.error = null
            emailOk = true
        }
    }

    private fun setUpOnClickListeners() {
        backImageView.setOnClickListener {
            finish()
        }

        createAccountButton.setOnClickListener {
            val name = nameTextInputEditText.text.toString()
            val email = emailTextInputEditText.text.toString()
            val password = passwordTextInputEditText.text.toString()
            validateEmail(email)
            validateName(name)
            validatePassword(password)
            if (acceptRulesCheckBox.isChecked) {
                if (nameOk && passwordOk && emailOk) {
                    progressBar.visibility = View.VISIBLE
                    val user = User(0, name, password, null, email, null)
                    CreateAccountAsyncTask(user, this).execute()
                }
            } else {
                Toast.makeText(this, getString(R.string.accept_rules), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpTheme() {
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }

    fun showSuccessAlert(user: User) {
        progressBar.visibility = View.GONE
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.success))
        builder.setIcon(getDrawable(R.drawable.ic_user))
        builder.setMessage(String.format(getString(R.string.user_created), user.name))
        builder.setPositiveButton(
            getString(android.R.string.ok)
        ) { _, _ -> finish() }
        builder.create().show()
    }

    fun showUserExistsError() {
        progressBar.visibility = View.GONE
        nameTextInputLayout.error = getString(R.string.user_exists)
    }

}
