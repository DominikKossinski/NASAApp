package com.example.nasa_app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.example.nasa_app.BuildConfig
import com.example.nasa_app.R
import com.example.nasa_app.User
import com.example.nasa_app.architecture.BaseActivity
import com.example.nasa_app.asynctasks.LoginAsyncTask
import com.example.nasa_app.databinding.ActivityLauncherBinding
import com.google.gson.GsonBuilder

class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {

    var connected = false

    companion object {
        const val SERVER_IP = "10.0.2.2:8080"
    }

    private val netReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isConnected(networkInfo)
        }

    }

    fun isConnected(networkInfo: NetworkInfo?) {
        connected = networkInfo != null && networkInfo.isConnected
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
        val email = preferences.getString("email", "")!!
        val id = preferences.getLong("id", -1)
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:LauncherActivity", "Name: '$name', Password: '$password' email:'$email' id: $id")
        }
        val countDownTimer = object : CountDownTimer(2000, 500) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                openMainActivity(User(1, "Name", "Password", null,  "Email", null), "")
                //TODO
//                if (connected || (name.compareTo("") != 0 && password.compareTo("") != 0 && !email.contentEquals("") && id != (-1).toLong())) {
//                    openNextActivity(name, password, email, id)
//                } else {
//                    openLoginActivity()
//                }

            }

        }
        countDownTimer.start()
    }

    private fun openNextActivity(name: String, password: String, email: String, id: Long) {

        if (name.compareTo("") != 0 && password.compareTo("") != 0 && !email.contentEquals("") && id != (-1).toLong()) {
            val gson = GsonBuilder().create()
            val user = gson.toJson(User(id, name, password, null, email, null))
            if (BuildConfig.DEBUG) {
                Log.d("LauncherActivity", "User: $user")
            }
            if (connected) {
                LoginAsyncTask(this@LauncherActivity, user).execute()
            } else {
                openMainActivity(User(id, name, password, null, email, null), "")
            }
        } else {
            openLoginActivity()
        }
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

    override fun onResume() {
        super.onResume()
        registerReceiver(netReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(netReceiver)
    }
}
