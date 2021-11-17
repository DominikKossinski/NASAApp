package com.example.nasa_app.architecture

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.nasa_app.utils.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    protected val firebaseAuth = FirebaseAuth.getInstance()
    protected val currentUser = firebaseAuth.currentUser

    protected lateinit var preferencesHelper: PreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val vbClass = vbType as Class<VB>
        val method = vbClass.getMethod(
            "inflate",
            LayoutInflater::class.java
        )
        binding = method.invoke(null, layoutInflater) as VB
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)
    }
}