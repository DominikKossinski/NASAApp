package com.example.nasa_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.example.nasa_app.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        setContentView(R.layout.activity_login)
        val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)

        loginButton.setOnClickListener {
            val editor = preferences.edit()
            val apiKey = apiKeyTextInputEditText.text.toString()
            if (rememberMeCheckBox.isChecked) {
                editor.putString("apiKey", apiKey)
            } else {
                editor.putString("apiKey", apiKey)
            }
            editor.apply()
            openMainActivity(apiKey)
        }
    }

    private fun openMainActivity(apiKey: String) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("apiKey", apiKey)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}
