package com.primapp.ui.post.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemListPostBinding
import com.primapp.model.*
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

    fun removePost(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            notifyItemRemoved(position)
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
                    onItemClick(LikePost(it.community.id, it.id, it.isLike, it.community.isJoined))
                }
            }

            binding.ivComment.setOnClickListener {
                data?.let {
                    onItemClick(CommentPost(it))
                }
            }

            binding.tvCommentCount.setOnClickListener {
                data?.let {
                    onItemClick(CommentPost(it))
                }
            }

            binding.ivMore.setOnClickListener {
                //creating a popup menu
                val popup = PopupMenu(binding.root.context, binding.ivMore)
                //inflating menu from xml resource
                if (data!!.isCreatedByMe) {
                    popup.inflate(R.menu.post_edit_menu)
                } else {
                    popup.inflate(R.menu.post_report_menu)
                }
                //adding click listener
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(p0: MenuItem?): Boolean {
                        when (p0?.itemId) {
                            R.id.editPost -> {
                                onItemClick(EditPost(data))
                            }

                            R.id.deletePost -> {
                                onItemClick(DeletePost(data))
                            }
                            R.id.hidePost -> {
                                onItemClick(HidePost(data))
                            }
                            R.id.reportPost -> {
                                onItemClick(ReportPost(data))
                            }
                        }

                        return false
                    }

                })
                //displaying the popup
                popup.show()
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