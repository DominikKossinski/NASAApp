package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import android.support.v4.widget.SwipeRefreshLayout
import com.example.nasa_app.Article
import com.example.nasa_app.ArticlesRVAdapter
import com.example.nasa_app.DBHelper
import java.net.URL
import java.util.concurrent.Semaphore
import javax.net.ssl.HttpsURLConnection

class GetImageAsyncTask(
    private val dbHelper: DBHelper,
    private val article: Article,
    private val adapter: ArticlesRVAdapter,
    private val semaphore: Semaphore,
    private val getArticleTasks: ArrayList<GetArticleAsyncTask>,
    private val getImageTasks: ArrayList<GetImageAsyncTask>,
    private val swipeRefreshLayout: SwipeRefreshLayout
) : AsyncTask<String, ByteArray?, ByteArray?>() {

    override fun doInBackground(vararg params: String?): ByteArray? {
        val url = URL(article.url)
        val connection = url.openConnection() as HttpsURLConnection
        connection.connect()


        if (connection.responseCode == 200) {
            return url.readBytes()
        }
        return null
    }

    override fun onPostExecute(result: ByteArray?) {
        article.drawable = result
        dbHelper.insertArticle(article)
        adapter.notifyDataSetChanged()

        semaphore.acquire()
        var done = true
        for (task in getArticleTasks) {
            if (task.status != Status.FINISHED) {
                done = false
                break
            }
        }
        if (done) {
            for (task in getImageTasks) {
                if (task != this) {
                    if (task.status != Status.FINISHED) {
                        done = false
                        break
                    }
                }
            }
        }
        swipeRefreshLayout.isRefreshing = !done
        semaphore.release()
        super.onPostExecute(result)
    }

}
