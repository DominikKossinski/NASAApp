package com.example.nasa_app.activities.main

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.nasa_app.MainNavGraphDirections
import com.example.nasa_app.R
import com.example.nasa_app.architecture.BaseActivity
import com.example.nasa_app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.navView.setNavigationItemSelectedListener(this)

        val headerView = binding.navView.getHeaderView(0)
        val emailTextView = headerView.findViewById<TextView>(R.id.userEmailTextView)
        emailTextView.text = viewModel.getUserEmail()
        collectFlow()
    }

    private fun collectFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.signOutFlow.collect {
                when (findNavController(R.id.mainNavHostFragment).graph.id) {
                    R.id.main_nav_graph -> {
                        Navigation.findNavController(this@MainActivity, R.id.mainNavHostFragment)
                            .navigate(MainNavGraphDirections.goToLoginActivity())
                    }
                }
                finish()
            }
        }
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
            R.id.nav_settings -> {
                findNavController(R.id.mainNavHostFragment).navigate(MainNavGraphDirections.toSettingsFragment())
            }
            R.id.nav_logout -> {
                viewModel.signOut()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
