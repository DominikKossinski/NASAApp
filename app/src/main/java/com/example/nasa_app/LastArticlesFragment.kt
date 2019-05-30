package com.example.nasa_app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nasa_app.asynctasks.GetArticleAsyncTask
import com.example.nasa_app.asynctasks.GetImageAsyncTask
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DBHelper(activity!!)
        articles = dbHelper.getAllArticles()
        if (BuildConfig.DEBUG) {
            Log.d("LastArticlesFragment", "ArticlesSize = ${articles.size}")
        }
        adapter = ArticlesRVAdapter(articles, activity!!, articlesSwipeRefreshLayout)
        articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        articlesRecyclerView.adapter = adapter


        articlesSwipeRefreshLayout.isRefreshing = true
        val missingDates = dbHelper.getMissingLastArticles()
        if (BuildConfig.DEBUG) {
            Log.d("LastArticlesFragment", "Missing Dates Size: ${missingDates.size}")
            Log.d("LastArticlesFragment", "Missing Dates: $missingDates")
        }
        adapter!!.notifyDataSetChanged()
        articlesSwipeRefreshLayout.isRefreshing = missingDates.size != 0

        val semafor = Semaphore(1)
        val getArticleTasks = ArrayList<GetArticleAsyncTask>()
        val getImageTasks = ArrayList<GetImageAsyncTask>()
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
                apiKey
            ).execute()
        }
        //TODO refresh
        articlesSwipeRefreshLayout.setOnRefreshListener {

        }


    }

    // TODO: Rename method, update argument and hook method into UI event
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
            user.apiKey!!
        )
        getArticlesTasks.add(task)
        task.execute()
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(apiKey: String): LastArticlesFragment {
            val fragment = LastArticlesFragment()
            fragment.apiKey = apiKey
            return fragment
        }
    }
}
