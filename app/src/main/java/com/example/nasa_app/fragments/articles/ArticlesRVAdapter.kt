package com.example.nasa_app.fragments.articles

import com.bumptech.glide.Glide
import com.example.nasa_app.DBHelper
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.NasaArticle
import com.example.nasa_app.architecture.BaseRecyclerViewAdapter
import com.example.nasa_app.databinding.ItemArticleBinding

class ArticlesRVAdapter : BaseRecyclerViewAdapter<NasaArticle, ItemArticleBinding>() {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemArticleBinding>, position: Int) {
        val article = items[position]
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
            onClickListener?.invoke(article)
        }
    }
}