package com.primapp.ui.post.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPostCommentBinding
import com.primapp.model.LikeComment
import com.primapp.model.comment.CommentData
import javax.inject.Inject

class CommentsListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<CommentData, CommentsListPagedAdapter.CommentsViewHolder>(PostListDiffCallback()) {

    fun markCommentAsLiked(commentId: Int?) {
        val item: CommentData? = snapshot().items.find { it.id == commentId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = true
            snapshot().items[position].likeCount++
            notifyItemChanged(position)
        }
    }

    fun markCommentAsDisliked(commentId: Int?) {
        val item: CommentData? = snapshot().items.find { it.id == commentId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = false
            snapshot().items[position].likeCount--
            notifyItemChanged(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_post_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class CommentsViewHolder(val binding: ItemPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommentData) {
            binding.data = data

            binding.tvCommentLike.setOnClickListener {
                onItemClick(LikeComment(data))
            }

            binding.tvCommentReply.setOnClickListener {
                onItemClick(data)
            }

            binding.tvRepliesCount.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    private class PostListDiffCallback : DiffUtil.ItemCallback<CommentData>() {
        override fun areItemsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem == newItem
        }
    }
}