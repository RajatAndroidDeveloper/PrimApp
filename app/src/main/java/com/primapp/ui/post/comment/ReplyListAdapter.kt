package com.primapp.ui.post.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPostCommentReplyBinding
import com.primapp.model.LikeReply
import com.primapp.model.reply.ReplyData
import javax.inject.Inject

class ReplyListAdapter @Inject constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<ReplyListAdapter.ReplyViewHolder>() {

    val replyList = ArrayList<ReplyData>()

    fun addData(listData: ArrayList<ReplyData>) {
        replyList.clear()
        replyList.addAll(listData)
        notifyDataSetChanged()
    }

    fun markReplyAsLiked(replyId: Int?) {
        val item: ReplyData? = replyList.find { it.id == replyId }
        item?.let {
            val position = replyList.indexOf(it)
            replyList.get(position).isLike = true
            var likeCount = replyList[position].likeCount ?: 0L
            replyList[position].likeCount = ++likeCount
            notifyItemChanged(position)
        }
    }

    fun markReplyAsDisliked(replyId: Int?) {
        val item: ReplyData? = replyList.find { it.id == replyId }
        item?.let {
            val position = replyList.indexOf(it)
            replyList.get(position).isLike = false
            var likeCount = replyList[position].likeCount ?: 0L
            replyList[position].likeCount = --likeCount
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReplyViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_post_comment_reply,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if (replyList.size > 1) 1 else replyList.size

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bind(replyList[position])
    }

    inner class ReplyViewHolder(val binding: ItemPostCommentReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReplyData) {
            binding.data = data

            binding.tvReplyLike.setOnClickListener {
                onItemClick(LikeReply(data))
            }
        }
    }
}