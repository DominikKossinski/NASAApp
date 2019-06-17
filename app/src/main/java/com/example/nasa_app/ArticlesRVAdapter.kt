package com.example.nasa_app

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.content.res.AppCompatResources.getDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.nasa_app.activities.MainActivity

class ArticlesRVAdapter(
    var articles: ArrayList<Article>,
    private val activity: Activity,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val noArticlesLayout: LinearLayout
) :
    RecyclerView.Adapter<ArticlesRVAdapter.ArticleViewHolder>() {
    override fun onCreateViewHolder(parerent: ViewGroup, p1: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parerent.context).inflate(R.layout.article_row, parerent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(viewHolder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        viewHolder.titleTextView!!.text = article.title
        viewHolder.dateTextView!!.text = DBHelper.simpleDateFormat.format(article.date)
        if (article.mediaType == ArticleMediaType.IMAGE) {
            if (article.drawable != null) {
                val drawable = BitmapDrawable(
                    activity.resources,
                    BitmapFactory.decodeByteArray(article.drawable, 0, article.drawable!!.size)
                )
                viewHolder.rowImageView!!.background = drawable
            } else {
                viewHolder.rowImageView!!.background = getDrawable(activity, R.drawable.ic_image_24dp)
            }
        } else {
            viewHolder.rowImageView!!.background = getDrawable(activity, R.drawable.ic_video_24dp)
        }
        viewHolder.itemView.setOnClickListener {
            if (activity is MainActivity) {
                if (swipeRefreshLayout.isRefreshing) {
                    Toast.makeText(activity, activity.getString(R.string.is_refreshing), Toast.LENGTH_SHORT).show()
                } else {
                    activity.openArticleActivity(article)
                }
            }
        }
    }

    fun sortNotify() {
        if (articles.size == 0) {
            noArticlesLayout.visibility = View.VISIBLE
        } else {
            noArticlesLayout.visibility = View.GONE
        }
        articles.sort()
        this.notifyDataSetChanged()
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var titleTextView: TextView? = null
        var dateTextView: TextView? = null
        var rowImageView: ImageView? = null

        init {
            titleTextView = itemView.findViewById(R.id.titleTextView)
            dateTextView = itemView.findViewById(R.id.dateTextView)
            rowImageView = itemView.findViewById(R.id.rowImageView)
        }
    }
}