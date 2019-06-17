package com.example.nasa_app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.nasa_app.*
import com.example.nasa_app.asynctasks.LoginAsyncTask
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_last_articles.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    LastArticlesFragment.OnFragmentInteractionListener {

    private var lastFragment: LastArticlesFragment? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var dbHelper: DBHelper? = null
    private var menu: Menu? = null
    var user: User? = null
    var connected = false
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    companion object {
        var mainActivity: MainActivity? = null
    }

    override fun onFragmentInteraction(uri: Uri) {
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
        if (lastFragment != null) {
            lastFragment!!.setConnectedNet(connected)
        }
        if (!connected) {
            Snackbar.make(fab, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
        } else {
            if (user!!.apiKey!!.contentEquals("")) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:MainActivity", "Login from MainActivity")
                }
                val userData = gson.toJson(User(0, user!!.name, user!!.password, null, "", null))
                LoginAsyncTask(this, userData).execute()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nightModeFlags = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        setContentView(R.layout.activity_main)


        dbHelper = DBHelper(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.popupTheme = R.style.NightTheme_PopupOverlay
        }

        val bundle = intent.extras
        if (bundle != null) {
            getDataFromBundle(bundle)
        }


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            run {
                showAddDialog()
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.userNameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.userEmailTextView)
        nameTextView.text = user!!.name
        emailTextView.text = user!!.email

        mainActivity = this
    }

    private fun showAddDialog() {
        if (articlesSwipeRefreshLayout.isRefreshing) {
            Toast.makeText(this, getString(R.string.is_refreshing), Toast.LENGTH_SHORT).show()
        } else {
            if (connected) {
                val dialog = AddDialogFragment.newInstance(this)
                dialog.show(supportFragmentManager, "add dialog")
            } else {
                Snackbar.make(fab, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
            }
        }


    }

    fun getDataFromBundle(bundle: Bundle, login: Boolean = false) {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:MainActivity", "getDataFromBundle(login = $login)")
        }
        AppService.jsessionid = bundle.getString("JSESSIONID", "")
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
        if (lastFragment != null) {
            lastFragment!!.user = user
        }
        if (login) {
            if (connected) {
                lastFragment!!.setConnectedNet(connected)
            }
            Snackbar.make(fab, getString(R.string.logged_in), Snackbar.LENGTH_LONG).show()
        }
        setUpFirstFragment(user!!.apiKey!!)
    }

    private fun setUpFirstFragment(apiKey: String) {
        lastFragment = LastArticlesFragment.newInstance(apiKey, user!!, connected)
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, lastFragment!!).commit()
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.menu = menu
        val myActionMenuItem = menu.findItem(R.id.action_search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(pattern: String?): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("MainActivity", "Querry: '${pattern!!}'")
                }
                if (lastFragment is LastArticlesFragment) {
                    val lastArticlesFragment = lastFragment as LastArticlesFragment
                    lastArticlesFragment.adapter!!.articles =
                        dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase(), user!!)
                    lastArticlesFragment.adapter!!.sortNotify()
                }
                return true
            }

            override fun onQueryTextChange(pattern: String?): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("MainActivity", "Querry: '${pattern!!}'")
                }
                if (lastFragment is LastArticlesFragment) {
                    val lastArticlesFragment = lastFragment as LastArticlesFragment
                    lastArticlesFragment.adapter!!.articles =
                        dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase(), user!!)
                    lastArticlesFragment.adapter!!.sortNotify()
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_last_articles -> {
                // Handle the camera action
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
        if (lastFragment is LastArticlesFragment) {
            if (articlesSwipeRefreshLayout.isRefreshing) {
                Toast.makeText(this, getString(R.string.is_refreshing), Toast.LENGTH_SHORT).show()
            } else {
                val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("name", "")
                editor.putString("password", "")
                //dbHelper!!.clearData(user!!)
                editor.apply()
                openLoginActivity()
            }
        }

    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun openArticleActivity(article: Article) {
        val intent = Intent(this, ArticleActivity::class.java)
        val bundle = Bundle()
        bundle.putString("date", simpleDateFormat.format(article.date))
        bundle.putString("JSESSIONID", AppService.jsessionid)
        bundle.putLong("userId", user!!.id)
        bundle.putString("name", user!!.name)
        bundle.putString("password", user!!.password)
        bundle.putString("role", user!!.role)
        bundle.putString("email", user!!.email)
        bundle.putString("apiKey", user!!.apiKey)
        intent.putExtras(bundle)
        startActivity(intent)

    }

    fun getArticleByDate(date: String) {
        if (!dbHelper!!.existsArticle(date, user!!)) {
            if (lastFragment is LastArticlesFragment) {
                (lastFragment as LastArticlesFragment).getArticleByDate(date, dbHelper!!, user!!)
            }
        } else {
            Snackbar.make(findViewById(R.id.main), R.string.already_exists, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(netReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(netReceiver)
    }

    override fun onDetachedFromWindow() {
        Log.d("MyLog:MainActivity", "onDetachedFromWindow()")
        super.onDetachedFromWindow()
    }

    override fun onStop() {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:MainActivity", "onStop()")
        }
        super.onStop()
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
