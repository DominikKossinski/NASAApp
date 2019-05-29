package com.example.nasa_app

import android.os.AsyncTask
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.concurrent.Semaphore
import javax.net.ssl.HttpsURLConnection

class GetArticleAsyncTask(
    private val dbHelper: DBHelper,
    private val articleArrayList: ArrayList<Article>,
    private val dateString: String,
    private val adapter: ArticlesRVAdapter,
    private val semaphore: Semaphore,
    private val getArticleTasks: ArrayList<GetArticleAsyncTask>,
    private val getImageTasks: ArrayList<GetImageAsyncTask>,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val apiKey: String
) : AsyncTask<String, Article, Article>() {
    override fun doInBackground(vararg params: String?): Article? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val url = URL("https://api.nasa.gov/planetary/apod?api_key=$apiKey&date=$dateString")
        val connection = url.openConnection() as HttpsURLConnection
        connection.connect()


        if (connection.responseCode == 200) {
            val response = url.readText()
            if (BuildConfig.DEBUG) {
                Log.d("GetArticleAsyncTask", "Resp: $response")
            }
            val jsonResponse = JSONObject(response)
            var mediaType = ArticleMediaType.IMAGE
            if (jsonResponse.getString("media_type")!!.contentEquals("video")) {
                mediaType = ArticleMediaType.VIDEO
            }
            val article = Article(
                jsonResponse.getString("title"), jsonResponse.getString("explanation"),
                simpleDateFormat.parse(jsonResponse.getString("date")), mediaType,
                jsonResponse.getString("url")
            )
            if (article.mediaType == ArticleMediaType.IMAGE) {
                //TODO dopissać AsyncTask do pobierania zdj
                val task = GetImageAsyncTask(
                    dbHelper,
                    article,
                    adapter,
                    semaphore,
                    getArticleTasks,
                    getImageTasks,
                    swipeRefreshLayout
                )
                getImageTasks.add(task)
                task.execute()
            } else {
                dbHelper.insertArticle(article)
            }
            articleArrayList.add(
                article
            )
            connection.disconnect()
            return article
        }
        connection.disconnect()
        return null
    }

    override fun onPostExecute(article: Article?) {
        //TODO  sortowanie listy artykułów
        adapter.notifyDataSetChanged()
        semaphore.acquire()
        var done = true
        for (task in getArticleTasks) {
            if (task != this) {
                if (task.status != Status.FINISHED) {
                    done = false
                    break
                }
            }
        }
        if (done) {
            for (task in getImageTasks) {
                if (task.status != Status.FINISHED) {
                    done = false
                    break
                }
            }
        }
        swipeRefreshLayout.isRefreshing = !done
        semaphore.release()
        super.onPostExecute(article)
    }

}