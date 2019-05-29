package com.example.nasa_app.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.asynctasks.LoginAsyncTask
import com.google.gson.GsonBuilder

class LauncherActivity : AppCompatActivity() {

    companion object {
        const val SERVER_IP = "10.0.2.2:8080"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_launcher)

        val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
        val name = preferences.getString("name", "")!!
        val password = preferences.getString("password", "")!!
        if (BuildConfig.DEBUG) {
            Log.d("LauncherActivity", "Name: '$name', Password: '$password'")
        }
        val countDownTimer = object : CountDownTimer(2000, 500) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (name.compareTo("") != 0 && password.compareTo("") != 0) {
                    val gson = GsonBuilder().create()
                    val user = gson.toJson(User(0, name, password, null, "", null))
                    if (BuildConfig.DEBUG) {
                        Log.d("LauncherActivity", "User: $user")
                    }
                    LoginAsyncTask(this@LauncherActivity, user).execute()
                    //openMainActivity(apiKey)
                } else {
                    openLoginActivity()
                }

            }

        }
        countDownTimer.start()
    }

    fun openMainActivity(user: User, jsessionid: String) {
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
        finish()
    }

    fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
