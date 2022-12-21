package com.example.nasa_app.dialogs.comments

import com.example.nasa_app.api.nasa.ArticleComment
import com.example.nasa_app.architecture.BaseRecyclerViewAdapter
import com.example.nasa_app.databinding.ItemArticleCommentBinding

class CommentsRvAdapter : BaseRecyclerViewAdapter<ArticleComment, ItemArticleCommentBinding>() {

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemArticleCommentBinding>,
        position: Int
    ) {
        val comment = items[position]
        holder.binding.tvComment.text = comment.comment
        holder.binding.tvAuthor.text = comment.author.email //  TODO change to nick
    }
}