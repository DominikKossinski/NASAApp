package com.example.nasa_app.dialogs.comments

import androidx.core.view.isVisible
import com.example.nasa_app.R
import com.example.nasa_app.api.nasa.ArticleComment
import com.example.nasa_app.architecture.BaseRecyclerViewAdapter
import com.example.nasa_app.databinding.ItemArticleCommentBinding
import com.example.nasa_app.extensions.getCommentFormattedString

class CommentsRvAdapter : BaseRecyclerViewAdapter<ArticleComment, ItemArticleCommentBinding>() {

    var currentUserId: String? = null

    private var onEditClickListener: ((ArticleComment) -> Unit)? = null

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemArticleCommentBinding>,
        position: Int
    ) {
        val comment = items[position]
        holder.binding.tvAuthor.text = comment.author.email //  TODO change to nick
        holder.binding.tvEdit.isVisible = comment.author.id == currentUserId
        holder.binding.tvEdit.setOnClickListener {
            onEditClickListener?.invoke(comment)
        }
        holder.binding.tvDate.text =
            holder.itemView.context.getCommentFormattedString(comment.createdAt)
        holder.binding.tvComment.text = if (comment.isEdited) {
            holder.itemView.context.getString(
                R.string.comments_edited_comment_format, comment.comment
            )
        } else {
            comment.comment
        }
    }

    fun setOnEditClickListener(listener: (ArticleComment) -> Unit) {
        onEditClickListener = listener
    }
}