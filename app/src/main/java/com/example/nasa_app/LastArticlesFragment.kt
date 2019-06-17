package com.example.nasa_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nasa_app.asynctasks.GetArticleAsyncTask
import com.example.nasa_app.asynctasks.GetImageAsyncTask
import com.example.nasa_app.asynctasks.GetSavedArticlesAsyncTask
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_last_articles.*
import java.text.SimpleDateFormat
import java.util.concurrent.Semaphore


class LastArticlesFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var articles = ArrayList<Article>()
    var adapter: ArticlesRVAdapter? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val dayMilliseconds = 86400000
    private var apiKey = "DEMO_KEY"
    var user: User? = null
    var connected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articles = ArrayList()
        adapter = ArticlesRVAdapter(articles, activity!!, articlesSwipeRefreshLayout, noArticlesLinearLayout)


        articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        articlesRecyclerView.adapter = adapter

        articlesSwipeRefreshLayout.setOnRefreshListener {
            if (connected) {
                if (!AppService.jsessionid.contentEquals("")) {
                    refreshData()
                }
            } else {
                Snackbar.make(activity!!.fab, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
                articlesSwipeRefreshLayout.isRefreshing = false
            }

        }

        if (connected) {
            refreshData()
        } else {
            getDataFromDB(true)
        }

    }

    private fun refreshData() {
        if (connected) {
            articlesSwipeRefreshLayout.isRefreshing = true
            val dbHelper = DBHelper(activity!!)
            val semafor = Semaphore(1)
            val getImageTasks = ArrayList<GetImageAsyncTask>()
            val getArticleTasks = ArrayList<GetArticleAsyncTask>()
            val getSavedArticlesAsyncTask = GetSavedArticlesAsyncTask(
                user!!, AppService.jsessionid, dbHelper, this, semafor, getArticleTasks,
                getImageTasks, adapter!!, articlesSwipeRefreshLayout
            )
            getSavedArticlesAsyncTask.execute()
        }
    }

    fun getLastArticles(
        dbHelper: DBHelper
    ) {
        getDataFromDB(false)
        adapter!!.articles = articles
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:LastArticlesFrag", "ArticlesSize = ${articles.size}")
        }
        val missingDates = dbHelper.getMissingLastArticles(user!!)
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:LastArticlesFrag", "Missing Dates Size: ${missingDates.size}")
            Log.d("MyLog:LastArticlesFrag", "Missing Dates: $missingDates")
        }
        adapter!!.sortNotify()
        articlesSwipeRefreshLayout.isRefreshing = missingDates.size != 0
        if (connected) {
            val semafor = Semaphore(1)
            val getImageTasks = ArrayList<GetImageAsyncTask>()
            val getArticleTasks = ArrayList<GetArticleAsyncTask>()
            for (date in missingDates) {
                GetArticleAsyncTask(
                    dbHelper,
                    articles,
                    date,
                    adapter!!,
                    semafor,
                    getArticleTasks,
                    getImageTasks,
                    articlesSwipeRefreshLayout,
                    apiKey,
                    user!!
                ).execute()
            }
        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun getArticleByDate(date: String, dbHelper: DBHelper, user: User) {
        articlesSwipeRefreshLayout.isRefreshing = true
        val semaphore = Semaphore(1)
        val getArticlesTasks = ArrayList<GetArticleAsyncTask>()
        val getImageTasks = ArrayList<GetImageAsyncTask>()
        val task = GetArticleAsyncTask(
            dbHelper,
            articles,
            date,
            adapter!!,
            semaphore,
            getArticlesTasks,
            getImageTasks,
            articlesSwipeRefreshLayout,
            user.apiKey!!,
            user
        )
        getArticlesTasks.add(task)
        task.execute()
    }

    fun setConnectedNet(connected: Boolean) {
        this.connected = connected
        if (connected) {
            refreshData()
        } else {
            getDataFromDB(true)
        }
    }

    private fun getDataFromDB(end: Boolean) {
        articlesSwipeRefreshLayout.isRefreshing = true
        val dbHelper = DBHelper(activity!!)
        articles = dbHelper.getAllArticles(user!!)
        adapter!!.articles = articles
        adapter!!.sortNotify()
        articlesSwipeRefreshLayout.isRefreshing = !end
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(apiKey: String, user: User, connected: Boolean): LastArticlesFragment {
            val fragment = LastArticlesFragment()
            fragment.user = user
            fragment.apiKey = apiKey
            fragment.connected = connected
            return fragment
        }
    }
}
