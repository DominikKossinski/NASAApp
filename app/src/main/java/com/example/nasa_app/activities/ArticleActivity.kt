package com.example.nasa_app.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.nasa_app.Article
import com.example.nasa_app.ArticleMediaType
import com.example.nasa_app.DBHelper
import com.example.nasa_app.R
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.content_article.*
import java.text.SimpleDateFormat

class ArticleActivity : AppCompatActivity() {

    var article: Article? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

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

        val dbHelper = DBHelper(this)
        val bundle = intent.extras
        if (bundle != null) {
            val date = bundle.getString("date")
            article = dbHelper.getArticleByDate(date!!)
            if (article!!.mediaType == ArticleMediaType.IMAGE) {
                val drawable = BitmapDrawable(
                    resources,
                    BitmapFactory.decodeByteArray(article!!.drawable, 0, article!!.drawable!!.size)
                )
                collapsingToolbarLayout.background = drawable
            } else if (article!!.mediaType == ArticleMediaType.VIDEO) {
                //TODO znaleźć lepsz zdjęcie
                collapsingToolbarLayout.background = getDrawable(R.mipmap.space)
            }
            setSupportActionBar(toolbar)
            supportActionBar!!.title = simpleDateFormat.format(article!!.date)
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.saveArticle -> {
                //TODO dodać zapisywanie artykułu
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
