package com.primapp.ui.post.reply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPostCommentReplyBinding
import com.primapp.model.LikeReply
import com.primapp.model.reply.ReplyData
import javax.inject.Inject

class PostCommentReplyPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<ReplyData, PostCommentReplyPagedAdapter.CommentsViewHolder>(PostCommentReplyDiffCallback()) {

    fun markReplyAsLiked(commentId: Int?) {
        val item: ReplyData? = snapshot().items.find { it.id == commentId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = true
            notifyItemChanged(position)
        }
    }

    fun markReplyAsDisliked(commentId: Int?) {
        val item: ReplyData? = snapshot().items.find { it.id == commentId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = false
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