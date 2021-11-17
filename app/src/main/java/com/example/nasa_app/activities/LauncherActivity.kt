package com.example.nasa_app.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.example.nasa_app.activities.main.MainActivity
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
                openNextActivity()
            }

        }
        countDownTimer.start()
    }

    private fun openNextActivity() {
        if (currentUser == null || !currentUser.isEmailVerified) {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
