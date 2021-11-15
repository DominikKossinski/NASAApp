package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nasa_app.*
import com.example.nasa_app.activities.LauncherActivity
import com.example.nasa_app.fragments.articles.ArticlesFragment
import com.example.nasa_app.fragments.articles.ArticlesRVAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Semaphore

class GetSavedArticlesAsyncTask(
    val user: User, val jsessionid: String, val dbHelper: DBHelper, val fragment: Fragment,
    val semaphore: Semaphore,
    val getImageAsyncTasks: ArrayList<GetImageAsyncTask>,
    val adapter: ArticlesRVAdapter,
    val swipeRefreshLayout: SwipeRefreshLayout
) :
    AsyncTask<String, String, JsonObject?>() {

    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    override fun doInBackground(vararg params: String?): JsonObject? {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:GetSavedTask", "Getting saved user: $user jsessionid: $jsessionid")
        }
        val url = URL("http://${LauncherActivity.SERVER_IP}/api/${user.id}/articles")
        val connection = url.openConnection() as HttpURLConnection
        connection.addRequestProperty("Cookie", "JSESSIONID=$jsessionid;")
        connection.requestMethod = "GET"
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "*/*")
        connection.doInput = true
        connection.connect()

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = StringBuffer()
            BufferedReader(InputStreamReader(connection.inputStream)).use {
                // Container for input stream data
                var inputLine = it.readLine()
                // Add each line to the response container
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()

            }
            val respString = response.toString()
            connection.disconnect()
            return gson.fromJson(respString, JsonElement::class.java).asJsonObject

        }
        return null
    }

    override fun onPostExecute(result: JsonObject?) {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:GetSavedAsyncTask", "Result: $result")
        }
        if (result != null) {
            if (result.has("status")) {
                if (result.get("status").asString!!.contentEquals("ok")) {
                    dbHelper.clearSaved(user)
                    val array = result.get("articles").asJsonArray
                    for (element in array) {
                        val article = gson.fromJson(element, Article::class.java)
                        article.saved = true
                        if (dbHelper.existsArticle(
                                DBHelper.simpleDateFormat.format(article.date),
                                user
                            )
                        ) {
                            dbHelper.updateArticle(article, user)
                        } else {
                            if (article.mediaType == ArticleMediaType.IMAGE) {
                                val getImageAsyncTask = GetImageAsyncTask(
                                    dbHelper,
                                    article,
                                    adapter,
                                    semaphore,
                                    getImageAsyncTasks,
                                    swipeRefreshLayout,
                                    user,
                                    true,
                                    fragment
                                )
                                getImageAsyncTasks.add(getImageAsyncTask)
                                getImageAsyncTask.execute()
                            } else {
                                article.saved = true
                                dbHelper.insertArticle(article, user)
                            }
                        }


                    }
                    semaphore.acquire()
                    if (getImageAsyncTasks.size == 0) {
                        semaphore.release()
                        if (BuildConfig.DEBUG) {
                            Log.d("MyLog:GetSavedAsyncTask", "GetLastArticles")
                        }
                        if (fragment is ArticlesFragment) {
                            fragment.getLastArticles(dbHelper)
                        }
                    } else {
                        semaphore.release()
                    }
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d("MyLog:GetSavedAsyncTask", "$result")
                }
            }
        }
        super.onPostExecute(result)
    }


}