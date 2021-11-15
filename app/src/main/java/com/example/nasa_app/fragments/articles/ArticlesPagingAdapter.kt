package com.example.nasa_app.fragments.articles

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.nasa_app.DBHelper
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.architecture.BasePagingDataAdapter
import com.example.nasa_app.architecture.BaseRecyclerViewAdapter
import com.example.nasa_app.databinding.ItemArticleBinding

class ArticlesPagingAdapter :
    BasePagingDataAdapter<NasaArticle, ItemArticleBinding>(
        ArticlesItemCallback()
    ) {

    override fun onBindViewHolder(
        holder: BaseRecyclerViewAdapter.BaseViewHolder<ItemArticleBinding>,
        position: Int
    ) {
        val article = getItem(position) ?: return
        holder.binding.titleTextView.text = article.title
        holder.binding.dateTextView.text = DBHelper.simpleDateFormat.format(article.date)
        if (article.mediaType == NasaArticle.NasaMediaType.IMAGE) {
            Glide.with(holder.itemView)
                .load(article.hdurl)
                .placeholder(R.drawable.ic_image_24dp)
                .into(holder.binding.rowImageView)
        } else {
            Glide.with(holder.itemView)
                .load(R.drawable.ic_video_24dp)
                .into(holder.binding.rowImageView)
        }
        holder.binding.root.setOnClickListener {
//            onClickListener?.invoke(article) // TODO
        }
    }

}

class ArticlesItemCallback : DiffUtil.ItemCallback<NasaArticle>() {

    override fun areItemsTheSame(oldItem: NasaArticle, newItem: NasaArticle): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: NasaArticle, newItem: NasaArticle): Boolean {
        return oldItem == newItem
    }

}