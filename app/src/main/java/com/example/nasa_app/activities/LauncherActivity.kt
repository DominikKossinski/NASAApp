package com.example.nasa_app.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import com.example.nasa_app.R

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        setContentView(R.layout.activity_launcher)

        val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
        val apiKey = preferences.getString("apiKey", "")!!
        val countDownTimer = object : CountDownTimer(2000, 500) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (apiKey.compareTo("") != 0) {
                    openMainActivity(apiKey)
                } else {
                    openLoginActivity()
                }

            }

        }
        countDownTimer.start()
    }

    private fun openMainActivity(apiKey: String) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("apiKey", apiKey)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
