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

    private var dbHelper: DBHelper? = null
    var user: User? = null
    var connected = false

    private val netReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isConnected(networkInfo)
        }

    }

    fun isConnected(networkInfo: NetworkInfo?) {
        connected = networkInfo != null && networkInfo.isConnected
        //TODO
//        if (fragment != null) {
//            fragment!!.setConnectedNet(connected)
//        }
        if (!connected) {
            //TODO
            //Snackbar.make(binding.fab, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
        } else {
            if (user!!.apiKey!!.contentEquals("")) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:MainActivity", "Login from MainActivity")
                }
//              TODO  LoginAsyncTask(this, userData).execute()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DBHelper(this)

        val bundle = intent.extras
        if (bundle != null) {
            getDataFromBundle(bundle)
        }

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

    fun getDataFromBundle(bundle: Bundle, login: Boolean = false) {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:MainActivity", "getDataFromBundle(login = $login)")
        }
        val id = bundle.getLong("userId", 0)
        val name = bundle.getString("name", "")
        val password = bundle.getString("password", "")
        val role = bundle.getString("role", "")
        val email = bundle.getString("email", "")
        val apiKey = bundle.getString("apiKey", "")
        user = User(id, name, password, role, email, apiKey)
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:MainActivity", "getDataFromBundle(user = $user)")
        }
        if (login) {
            if (connected) {
                //TODO fragment!!.setConnectedNet(connected)
            }
            //TODO
            //Snackbar.make(binding.fab, getString(R.string.logged_in), Snackbar.LENGTH_LONG).show()
        }
    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // TODO Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        this.menu = menu
        val myActionMenuItem = menu.findItem(R.id.action_search)
        //TODO
//        val searchView = myActionMenuItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(pattern: String?): Boolean {
//                if (BuildConfig.DEBUG) {
//                    Log.d("MainActivity", "Querry: '${pattern!!}'")
//                }
//                if (lastFragment is LastArticlesFragment) {
//                    val lastArticlesFragment = lastFragment as LastArticlesFragment
//                    lastArticlesFragment.adapter!!.articles =
//                        dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase(), user!!)
//                    lastArticlesFragment.adapter!!.sortNotify()
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(pattern: String?): Boolean {
//                if (BuildConfig.DEBUG) {
//                    Log.d("MainActivity", "Querry: '${pattern!!}'")
//                }
//                if (lastFragment is LastArticlesFragment) {
//                    val lastArticlesFragment = lastFragment as LastArticlesFragment
//                    lastArticlesFragment.adapter!!.articles =
//                        dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase(), user!!)
//                    lastArticlesFragment.adapter!!.sortNotify()
//                }
//                return true
//            }
//
//        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
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

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getArticleByDate(date: String) {
        //TODO
//        if (!dbHelper!!.existsArticle(date, user!!)) {
//            if (fragment is ArticlesFragment) {
//                (fragment as ArticlesFragment).getArticleByDate(date, dbHelper!!, user!!)
//            }
//        } else {
//            Snackbar.make(findViewById(R.id.main), R.string.already_exists, Snackbar.LENGTH_SHORT)
//                .show()
//        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(netReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(netReceiver)
    }

    override fun onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:MainActivity", "onDestroy(1)")
        }
        /*  if(jsessionid!!.contentEquals("")) {
            Log.d("MyLog:MainActivity", "onDestroy(1)")
            super.onDestroy()
        } else {
            if(connected) {
                val semaphore = Semaphore(1)
                semaphore.acquire()
                LogOutAsyncTask(jsessionid!!, this, semaphore).execute()
                semaphore.acquire()
                Log.d("MyLog:MainActivity", "onDestroy(2)")
                super.onDestroy()
            } else {
                Log.d("MyLog:MainActivity", "onDestroy(3)")
                super.onDestroy()
            }
        }*/
        super.onDestroy()

    }
}
