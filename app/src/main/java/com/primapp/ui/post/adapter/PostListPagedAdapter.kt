package com.primapp.ui.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemListPostBinding
import com.primapp.model.post.PostListResult
import javax.inject.Inject

class PostListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<PostListResult, PostListPagedAdapter.PostsViewHolder>(PostListDiffCallback()) {

    fun markPostAsLiked(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = true
            snapshot().items.get(position).postLikes++
            notifyItemChanged(position)
        }
    }

    fun markPostAsDisliked(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isLike = false
            snapshot().items.get(position).postLikes--
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_list_post,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostsViewHolder(val binding: ItemListPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PostListResult?) {
            binding.data = data
            binding.cardPostAttachment.setOnClickListener {
                when (data!!.fileType) {
                    PostFileType.VIDEO -> {
                        onItemClick(ShowVideo(data.getImageUrl))
                    }
                    PostFileType.IMAGE -> {
                        onItemClick(ShowImage(data.getImageUrl))
                    }
                    PostFileType.GIF -> {
                        onItemClick(ShowImage(data.getImageUrl))
                    }
                }
            }

            binding.ivLike.setOnClickListener {
                data?.let {
                    onItemClick(LikePost(it.community.id, it.id, it.isLike))
                }
            }
        }
    }

    private class PostListDiffCallback : DiffUtil.ItemCallback<PostListResult>() {
        override fun areItemsTheSame(oldItem: PostListResult, newItem: PostListResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostListResult, newItem: PostListResult): Boolean {
            return oldItem == newItem
        }
    }
}

data class ShowImage(val url: String)
data class ShowVideo(val url: String)
data class LikePost(val communityId: Int, val postId: Int, val isLike: Boolean)