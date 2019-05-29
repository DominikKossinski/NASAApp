package com.example.nasa_app.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.asynctasks.LoginAsyncTask
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpTheme()
        setContentView(R.layout.activity_login)
        setUpTextWatchers()
        setUpOnClickListerers()
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

    private fun setUpOnClickListerers() {
        loginButton.setOnClickListener {
            val name = nameTextInputEditText.text.toString()
            val password = passwordTextInputEditText.text.toString()
            if (name.compareTo("") == 0) {
                nameTextInputLayout.error = getString(R.string.empty_name)
            }
            if (password.contentEquals("")) {
                passwordTextInputLayout.error = getString(R.string.empty_password)
            }
            if (!password.equals("") and !name.equals("")) {
                //Todo dodac alert ladowania
                progressBar.visibility = View.VISIBLE
                val gson = GsonBuilder().create()
                val user = gson.toJson(User(0, name, password, null, "", null))
                LoginAsyncTask(
                    this, user, nameTextInputLayout, passwordTextInputLayout,
                    nameTextInputEditText, passwordTextInputEditText, progressBar
                ).execute()
            }
        }
        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpTextWatchers() {
        nameTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().compareTo("") == 0) {
                    nameTextInputLayout.error = getString(R.string.empty_name)
                } else {
                    nameTextInputLayout.error = null
                }
            }

        })
        passwordTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().compareTo("") == 0) {
                    passwordTextInputLayout.error = getString(R.string.empty_password)
                } else {
                    passwordTextInputLayout.error = null
                }
            }

        })
    }

    fun openMainActivity(user: User, jsessionid: String) {
        val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        if (rememberMeCheckBox.isChecked) {
            editor.putString("name", user.name)
            editor.putString("password", passwordTextInputEditText.text.toString())
            editor.apply()
        } else {
            editor.putString("name", "")
            editor.putString("password", "")
            editor.apply()
        }
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("JSESSIONID", jsessionid)
        bundle.putLong("userId", user.id)
        bundle.putString("name", user.name)
        bundle.putString("password", user.password)
        bundle.putString("role", user.role)
        bundle.putString("email", user.email)
        bundle.putString("apiKey", user.apiKey)
        intent.putExtras(bundle)
        startActivity(intent)
        progressBar!!.visibility = View.GONE
        finish()
    }
}
