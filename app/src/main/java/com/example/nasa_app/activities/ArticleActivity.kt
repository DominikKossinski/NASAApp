package com.example.nasa_app.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.nasa_app.*
import com.example.nasa_app.asynctasks.DeleteArticleAsyncTask
import com.example.nasa_app.asynctasks.LoginAsyncTask
import com.example.nasa_app.asynctasks.SaveArticleAsyncTask
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.content_article.*

class ArticleActivity : AppCompatActivity() {

    var article: Article? = null
    var dbHelper: DBHelper? = null
    var user: User? = null
    var jsessionid: String? = null
    var connected = false
    var gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    private val netReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isConnected(networkInfo)
        }

    }

    fun isConnected(networkInfo: NetworkInfo?) {
        connected = networkInfo != null && networkInfo.isConnected
        if (!connected) {
            showNoInternetSnackBar()
        } else {
            if (user!!.apiKey!!.contentEquals("")) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:ArticleActivity", "Login from MainActivity")
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
        setContentView(R.layout.activity_article)
        setSupportActionBar(toolbar)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.popupTheme = R.style.NightTheme_PopupOverlay
        } else {
            toolbar.popupTheme = R.style.AppTheme_PopupOverlay
        }
        dbHelper = DBHelper(this)
        val bundle = intent.extras
        if (bundle != null) {
            setUpUserData(bundle)
            setUpArticleData(bundle)
        }
    }

    fun setUpUserData(bundle: Bundle, login: Boolean = false) {
        jsessionid = bundle.getString("JSESSIONID", "")
        val id = bundle.getLong("userId", 0)
        val name = bundle.getString("name", "")
        val password = bundle.getString("password", "")
        val role = bundle.getString("role", "")
        val email = bundle.getString("email", "")
        val apiKey = bundle.getString("apiKey", "")
        user = User(id, name, password, role, email, apiKey)
        if (login) {
            MainActivity.mainActivity!!.user = user!!
            AppService.jsessionid = jsessionid!!
            showNoInternetSnackBar()
        }
    }

    private fun showNoInternetSnackBar() {
        Snackbar.make(articleTextView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
    }


    private fun setUpArticleData(bundle: Bundle) {
        val date = bundle.getString("date")
        article = dbHelper!!.getArticleByDate(date!!, user!!)
        if (article!!.mediaType == ArticleMediaType.IMAGE) {
            val drawable = BitmapDrawable(
                resources,
                BitmapFactory.decodeByteArray(article!!.drawable, 0, article!!.drawable!!.size)
            )
            collapsingToolbarLayout.background = drawable
        } else if (article!!.mediaType == ArticleMediaType.VIDEO) {
            collapsingToolbarLayout.background = getDrawable(R.mipmap.space)
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.title = DBHelper.simpleDateFormat.format(article!!.date)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        articleTextView.text = article!!.explanation
        titleTextView.text = article!!.title
        if (article!!.mediaType == ArticleMediaType.VIDEO) {
            videoLinearLayout.visibility = View.VISIBLE
            videoLinearLayout.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article!!.url))
                startActivity(intent)
            }
        } else {
            videoLinearLayout.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_menu, menu)
        if (article!!.saved!!) {
            menu!!.findItem(R.id.saveArticle).isVisible = false
            menu.findItem(R.id.deleteArticle).isVisible = true
        } else {
            menu!!.findItem(R.id.saveArticle).isVisible = true
            menu.findItem(R.id.deleteArticle).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.saveArticle -> {
                if (connected) {
                    progressBar.visibility = View.VISIBLE
                    SaveArticleAsyncTask(user!!, article!!, this, jsessionid!!).execute()
                } else {
                    showNoInternetSnackBar()
                }
            }
            R.id.deleteArticle -> {
                if (connected) {
                    progressBar.visibility = View.VISIBLE
                    DeleteArticleAsyncTask(user!!, article!!, jsessionid!!, this).execute()
                } else {
                    showNoInternetSnackBar()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSavingEnd(saved: Boolean) {
        progressBar.visibility = View.GONE
        article!!.saved = saved
        val menu = toolbar.menu
        if (article!!.saved!!) {
            menu!!.findItem(R.id.saveArticle).isVisible = false
            menu.findItem(R.id.deleteArticle).isVisible = true
        } else {
            menu!!.findItem(R.id.saveArticle).isVisible = true
            menu.findItem(R.id.deleteArticle).isVisible = false
        }
        dbHelper!!.updateSaved(article!!, user!!)
        if (saved) {
            Toast.makeText(this, getString(R.string.success_full_saved), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.error_by_saving), Toast.LENGTH_LONG).show()
        }
    }

    fun showDeletingEnd(deleted: Boolean) {
        progressBar.visibility = View.GONE
        article!!.saved = !deleted
        val menu = toolbar.menu
        if (article!!.saved!!) {
            menu!!.findItem(R.id.saveArticle).isVisible = false
            menu.findItem(R.id.deleteArticle).isVisible = true
        } else {
            menu!!.findItem(R.id.saveArticle).isVisible = true
            menu.findItem(R.id.deleteArticle).isVisible = false
        }
        dbHelper!!.updateSaved(article!!, user!!)
        if (deleted) {
            Toast.makeText(this, getString(R.string.success_full_deleted), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.error_by_deleting), Toast.LENGTH_LONG).show()
        }
    }

    fun logOut() {
        MainActivity.mainActivity!!.logOut()
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
