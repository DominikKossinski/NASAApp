package com.example.nasa_app.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.example.nasa_app.architecture.BaseActivity
import com.example.nasa_app.databinding.ActivityLauncherBinding

class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO check firebase user
        val countDownTimer = object : CountDownTimer(2000, 500) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                openLoginActivity()
//                openMainActivity()
            }

        }
        countDownTimer.start()
    }


    fun openMainActivity() {
        //TODO move to navigation
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun openLoginActivity() {
        //TODO move to navigation
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
