package com.primapp.ui.post.reply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPostCommentReplyBinding
import com.primapp.model.DeleteReply
import com.primapp.model.LikeReply
import com.primapp.model.comment.CommentData
import com.primapp.model.reply.ReplyData
import javax.inject.Inject

class PostCommentReplyPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<ReplyData, PostCommentReplyPagedAdapter.CommentsViewHolder>(PostCommentReplyDiffCallback()) {

    fun markReplyAsLiked(replyId: Int?) {
        val item: ReplyData? = snapshot().items.find { it.id == replyId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = true
            var likeCount = snapshot().items[position].likeCount ?: 0L
            snapshot().items[position].likeCount = ++likeCount
            notifyItemChanged(position)
        }
    }

    fun removeReply(replyID: Int?) {
        val item: ReplyData? = snapshot().items.find { it.id == replyID }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }


    fun markReplyAsDisliked(replyId: Int?) {
        val item: ReplyData? = snapshot().items.find { it.id == replyId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = false
            var likeCount = snapshot().items[position].likeCount ?: 0L
            snapshot().items[position].likeCount = --likeCount
            notifyItemChanged(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_post_comment_reply,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class CommentsViewHolder(val binding: ItemPostCommentReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReplyData) {
            binding.data = data

            binding.tvReplyLike.setOnClickListener {
                onItemClick(LikeReply(data))
            }

            binding.llCommentPost.setOnClickListener {
                onItemClick(DeleteReply(data))
            }
        }
    }

    private class PostCommentReplyDiffCallback : DiffUtil.ItemCallback<ReplyData>() {
        override fun areItemsTheSame(oldItem: ReplyData, newItem: ReplyData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReplyData, newItem: ReplyData): Boolean {
            return oldItem == newItem
        }
    }
}