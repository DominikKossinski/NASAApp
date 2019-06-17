package com.example.nasa_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(
            "CREATE TABLE $ARTICLES_TABLE($USER_ID INTEGER,$ID INTEGER, $COUNT INTEGER, $DATE DATE, $TITLE VARCHAR(300), " +
                    "$EXPLANATION VARCHAR(2000), $MEDIA_TYPE VARCHAR(5), $ARTICLE_URL VARCHAR(300), $DRAWABLE_BYTES BLOB," +
                    "$SAVED INTEGER, PRIMARY KEY($USER_ID, $DATE))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE $ARTICLES_TABLE")
        onCreate(db)
    }

    companion object {
        const val MAX_DATES = 15.toLong()
        const val DB_NAME = "NASSA_DB"
        const val USER_ID = "USER_ID"
        const val ARTICLES_TABLE = "ARTICLES"
        const val ID = "ID"
        const val COUNT = "COUNT"
        const val TITLE = "TITLE"
        const val EXPLANATION = "EXPLANATION"
        const val DATE = "DATE"
        const val MEDIA_TYPE = "MEDIA_TYPE"
        const val ARTICLE_URL = "URL"
        const val DRAWABLE_BYTES = "DRAWABLE_BYTES"
        const val SAVED = "SAVED"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val dayMilliseconds = 86400000.toLong()
    }

    fun getMissingLastArticles(user: User): ArrayList<String> {

        val db = readableDatabase
        val projection = arrayOf(DATE)
        val selection = " $ID = 0 and $USER_ID = ${user.id}"
        val cursor = db.query(
            ARTICLES_TABLE, projection, selection, null,
            null, null, null, "30"
        )
        val dates = ArrayList<String>()
        while (cursor.moveToNext()) {
            val dateString = cursor.getString(cursor.getColumnIndex(DATE))
            val date = dateFormat.parse(dateString)
            dates.add(simpleDateFormat.format(date))
        }

        val missingDates = ArrayList<String>()
        for (i in 0.toLong() until MAX_DATES) {
            val date = Date(Date().time - i * dayMilliseconds)
            val currentDate = simpleDateFormat.format(date)
            if (!dates.contains(currentDate)) {
                missingDates.add(currentDate)
            }
        }
        cursor.close()
        return missingDates
    }

    fun insertArticle(article: Article, user: User): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:DBHelper", "Article: $article, user: $user")
        }
        val saved = if (article.saved!!) 1 else 0
        val db = writableDatabase
        if (article.mediaType == ArticleMediaType.IMAGE) {
            val count = ceil(article.drawable!!.size.toDouble() / 1000000.0).toInt()
            db.beginTransaction()
            val splitBytes = ByteArray(1000000)
            var toWrite = article.drawable!!.size
            for (i in 0 until count) {
                var length = 1000000
                if (toWrite < 1000000) {
                    length = toWrite
                }
                System.arraycopy(article.drawable!!, i * 1000000, splitBytes, 0, length)
                toWrite -= 1000000
                val values = ContentValues().apply {
                    put(USER_ID, user.id)
                    put(ID, i)
                    put(COUNT, count)
                    put(TITLE, article.title)
                    put(EXPLANATION, article.explanation)
                    put(DATE, dateFormat.format(article.date))
                    put(MEDIA_TYPE, article.mediaType.mediaType)
                    put(ARTICLE_URL, article.url)
                    put(DRAWABLE_BYTES, splitBytes)
                    put(SAVED, saved)
                }
                val result = db.insert(ARTICLES_TABLE, null, values)
                if (result < 0.toLong()) {
                    db.endTransaction()
                    return false
                }
            }
        } else {
            db.beginTransaction()
            val values = ContentValues().apply {
                put(USER_ID, user.id)
                put(ID, 0)
                put(COUNT, 1)
                put(TITLE, article.title)
                put(EXPLANATION, article.explanation)
                put(DATE, dateFormat.format(article.date))
                put(MEDIA_TYPE, article.mediaType.mediaType)
                put(ARTICLE_URL, article.url)
                put(DRAWABLE_BYTES, byteArrayOf(0x00))
                put(SAVED, saved)
            }
            val result = db.insert(ARTICLES_TABLE, null, values)
            if (result < 0.toLong()) {
                db.endTransaction()
                return false
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        return true
    }

    fun getAllArticles(user: User): ArrayList<Article> {
        val dates = getAllDates(user)
        val articles = ArrayList<Article>()
        for (date in dates) {
            articles.add(getArticleByDate(date, user)!!)
        }
        return articles
    }

    fun getArticleByDate(date: String, user: User): Article? {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:DBHelper", "Date: $date, user: $user")
        }
        val db = readableDatabase
        val projection = arrayOf(ID, COUNT, TITLE, EXPLANATION, DATE, MEDIA_TYPE, ARTICLE_URL, DRAWABLE_BYTES, SAVED)
        val selection = " date = '${dateFormat.format(simpleDateFormat.parse(date))}' and $USER_ID = ${user.id}"
        val orderBy = ID
        val cursor = db.query(
            ARTICLES_TABLE, projection, selection, null,
            null, null, orderBy
        )
        var article: Article? = null
        var bytes: ByteArray? = null
        var i = 0
        while (cursor.moveToNext()) {
            if (cursor.isFirst) {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:DBHelper", "Create article")
                }
                val title = cursor.getString(cursor.getColumnIndex(TITLE))
                val explanation = cursor.getString(cursor.getColumnIndex(EXPLANATION))
                var mediaType = ArticleMediaType.IMAGE
                if (cursor.getString(cursor.getColumnIndex(MEDIA_TYPE))!!.contentEquals("video")) {
                    mediaType = ArticleMediaType.VIDEO
                }
                val url = cursor.getString(cursor.getColumnIndex(ARTICLE_URL))
                val saved = cursor.getInt(cursor.getColumnIndex(SAVED)) == 1
                article = Article(title, explanation, simpleDateFormat.parse(date), mediaType, url, saved = saved)
                val count = cursor.getInt(cursor.getColumnIndex(COUNT))
                bytes = ByteArray(count * 1000000)
            }
            if (article!!.mediaType == ArticleMediaType.IMAGE) {
                val sBytes = cursor.getBlob(cursor.getColumnIndex(DRAWABLE_BYTES))
                System.arraycopy(sBytes, 0, bytes, i * 1000000, 1000000)
                i++
            }
        }
        if (bytes != null) {
            article!!.drawable = bytes
        }
        cursor.close()
        return article!!
    }

    private fun getAllDates(user: User): ArrayList<String> {
        val db = readableDatabase
        val projection = arrayOf(DATE)
        val selection = " $ID = 0 and $USER_ID = ${user.id}"
        val cursor = db.query(
            ARTICLES_TABLE, projection, selection, null,
            null, null, null
        )
        val dates = ArrayList<String>()
        while (cursor.moveToNext()) {
            val dateString = cursor.getString(cursor.getColumnIndex(DATE))
            val date = dateFormat.parse(dateString)
            dates.add(simpleDateFormat.format(date))
        }
        cursor.close()
        return dates
    }

    fun getArticlesByPattern(pattern: String, user: User): ArrayList<Article> {
        val allArticles = getAllArticles(user)
        val articles = ArrayList<Article>()
        for (article in allArticles) {
            if (article.title.toLowerCase().contains(pattern) || simpleDateFormat.format(article.date).toLowerCase().contains(
                    pattern
                )
            ) {
                articles.add(article)
            }
        }
        return articles
    }

    fun existsArticle(date: String, user: User): Boolean {
        val db = readableDatabase
        val projection = arrayOf(ID)
        val selection =
            " date = '${dateFormat.format(simpleDateFormat.parse(date))}' and $ID = 0 and $USER_ID = ${user.id}"
        val cursor = db.query(
            ARTICLES_TABLE, projection, selection, null,
            null, null, null
        )
        val bool = cursor.count == 1
        cursor.close()
        return bool
    }

    fun updateSaved(article: Article, user: User) {
        val db = writableDatabase
        val saved = if (article.saved!!) 1 else 0
        val values = ContentValues().apply {
            put(SAVED, saved)
        }
        val selection = " $DATE = '${dateFormat.format(article.date)}' and $USER_ID = ${user.id}"
        db.update(ARTICLES_TABLE, values, selection, null)
    }

    fun updateArticle(article: Article, user: User) {
        if (!existsArticle(simpleDateFormat.format(article.date), user)) {
            insertArticle(article, user)
        } else {
            updateSaved(article, user)
        }
    }

    fun clearData(user: User) {
        val db = writableDatabase
        db.delete(ARTICLES_TABLE, " $USER_ID = ${user.id}", null)
    }

    fun clearSaved(user: User) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(SAVED, 0)
        }
        val selection = " $USER_ID = ${user.id}"
        val count = db.update(ARTICLES_TABLE, values, selection, null)
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:DBHelper", "Clear saved = $count")
        }
    }


}