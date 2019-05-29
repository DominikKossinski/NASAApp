package com.example.nasa_app.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
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
import com.example.nasa_app.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    LastArticlesFragment.OnFragmentInteractionListener {

    private var lastFragment: Fragment? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var dbHelper: DBHelper? = null
    private var menu: Menu? = null

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        //TODO get user data
        dbHelper = DBHelper(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.popupTheme = R.style.NightTheme_PopupOverlay
        }

        val bundle = intent.extras
        val apiKey = if (bundle != null) {
            bundle.getString("apiKey")
        } else {
            "DEMO_KEY"
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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
        setUpFirstFragment(apiKey)

    }

    private fun setUpFirstFragment(apiKey: String) {
        lastFragment = LastArticlesFragment.newInstance(apiKey)
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
                    lastArticlesFragment.adapter!!.articles = dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase())
                    lastArticlesFragment.adapter!!.notifyDataSetChanged()
                    //TODO zrobić brak artykułów
                }
                return true
            }

            override fun onQueryTextChange(pattern: String?): Boolean {
                if (BuildConfig.DEBUG) {
                    Log.d("MainActivity", "Querry: '${pattern!!}'")
                }
                if (lastFragment is LastArticlesFragment) {
                    val lastArticlesFragment = lastFragment as LastArticlesFragment
                    lastArticlesFragment.adapter!!.articles = dbHelper!!.getArticlesByPattern(pattern!!.toLowerCase())
                    lastArticlesFragment.adapter!!.notifyDataSetChanged()
                    //TODO zrobić brak artykułów
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            R.id.nav_logout -> {
                logOut()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOut() {
        val preferences = getSharedPreferences("com.example.nasa_app.MyPref", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("name", "")
        editor.putString("password", "")
        editor.apply()
        openLoginActivity()
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
        intent.putExtras(bundle)
        startActivity(intent)

    }
}
