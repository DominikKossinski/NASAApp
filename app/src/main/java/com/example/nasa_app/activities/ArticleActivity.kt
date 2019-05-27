package com.example.nasa_app.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import com.example.nasa_app.Article
import com.example.nasa_app.ArticleMediaType
import com.example.nasa_app.R
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.content_article.*
import java.text.SimpleDateFormat

class ArticleActivity : AppCompatActivity() {

    var article: Article? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.NightTheme_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        setContentView(R.layout.activity_article)
        setSupportActionBar(toolbar)


        val bundle = intent.extras
        if (bundle != null) {
            val byteArray = bundle.getByteArray("image")
            val title = bundle.getString("title")!!
            val explanation = bundle.getString("explanation")!!
            val date = simpleDateFormat.parse(bundle.getString("date"))
            val mediaType = if (bundle.getString("mediaType")!!.compareTo("image") == 0) {
                ArticleMediaType.IMAGE
            } else {
                ArticleMediaType.VIDEO
            }
            val url = bundle.getString("url")!!
            val hdUrl = bundle.getString("hdUrl")
            article = Article(title, explanation, date, mediaType, url, hdUrl, byteArray)
            if (byteArray != null) {
                val drawable = BitmapDrawable(resources, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                collapsingToolbarLayout.background = drawable
            } else {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
