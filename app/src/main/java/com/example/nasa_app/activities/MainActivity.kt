package com.example.nasa_app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.nasa_app.*
import com.example.nasa_app.architecture.BaseActivity
import com.example.nasa_app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.navView.setNavigationItemSelectedListener(this)

//TODO        val headerView = binding.navView.getHeaderView(0)
//        val nameTextView = headerView.findViewById<TextView>(R.id.userNameTextView)
//        val emailTextView = headerView.findViewById<TextView>(R.id.userEmailTextView)
//        nameTextView.text = user!!.name
//        emailTextView.text = user!!.email

    }

    fun setupDrawer(toolbar: Toolbar) {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_articles -> {
                findNavController(R.id.mainNavHostFragment).navigate(MainNavGraphDirections.toArticles())
            }
            R.id.nav_saved_articles -> {
                findNavController(R.id.mainNavHostFragment).navigate(MainNavGraphDirections.toSavedArticles())
            }
            R.id.nav_logout -> {
                logOut()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun logOut() {
        //TODO if (fragment is ArticlesFragment) {
        //TODO
//            if (binding.articlesSwipeRefreshLayout.isRefreshing) {
//                Toast.makeText(this, getString(R.string.is_refreshing), Toast.LENGTH_SHORT).show()
//            } else {
//                val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
//                val editor = preferences.edit()
//                editor.putString("name", "")
//                editor.putString("password", "")
//                //dbHelper!!.clearData(user!!)
//                editor.apply()
//                openLoginActivity()
//            }
//        }

    }

}
