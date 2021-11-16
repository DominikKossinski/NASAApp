package com.example.nasa_app.asynctasks

import android.os.AsyncTask
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nasa_app.*
import com.example.nasa_app.fragments.articles.ArticlesFragment
import com.example.nasa_app.fragments.saved_articles.adapter.ArticlesRVAdapter
import java.net.URL
import java.util.concurrent.Semaphore
import javax.net.ssl.HttpsURLConnection

class GetImageAsyncTask(
    private val dbHelper: DBHelper,
    private val article: Article,
    private val adapter: ArticlesRVAdapter,
    private val semaphore: Semaphore,
    private val getImageTasks: ArrayList<GetImageAsyncTask>,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val user: User,
    private val update: Boolean = false,
    private val fragment: Fragment? = null
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
        dbHelper.insertArticle(article, user)

//        TODO adapter.sortNotify()

        semaphore.acquire()
        var done = true
//        for (task in getArticleTasks) {
//            if (task.status != Status.FINISHED) {
//                done = false
//                break
//            }
//        }
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
        if (update) {
            if (fragment is ArticlesFragment) {
                fragment.getLastArticles(dbHelper)
            }
        } else {
            swipeRefreshLayout.isRefreshing = !done
        }
        semaphore.release()
        super.onPostExecute(result)
    }

}
