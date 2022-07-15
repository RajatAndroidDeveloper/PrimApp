package com.primapp.ui.post.reported_post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemSimplePostBinding
import com.primapp.model.*
import com.primapp.model.post.PostListResult
import javax.inject.Inject

class SimplePostListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<PostListResult, SimplePostListPagedAdapter.SimplePostsViewHolder>(SimplePostListDiffCallback()) {

    private var currentUserId: Int = -1

    public fun setCurrentLoggedInUser(userId: Int) {
        currentUserId = userId
    }

    fun removePost(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplePostsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SimplePostsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_simple_post,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SimplePostsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimplePostsViewHolder(val binding: ItemSimplePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PostListResult?) {
            binding.data = data
            binding.cardPostAttachment.setOnClickListener {
                when (data!!.fileType) {
                    PostFileType.VIDEO -> {
                        onItemClick(ShowVideo(data.imageUrl.toString()))
                    }
                    PostFileType.IMAGE -> {
                        onItemClick(ShowImage(data.imageUrl.toString()))
                    }
                    PostFileType.GIF -> {
                        onItemClick(ShowImage(data.imageUrl.toString()))
                    }
                    PostFileType.FILE -> {
                        onItemClick(DownloadFile(data.imageUrl.toString()))
                    }
                }
            }

            binding.ivMore.setOnClickListener {
                //creating a popup menu
                val popup = PopupMenu(binding.root.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.reported_post_action_menu)
                if (currentUserId != -1)
                    popup.menu.findItem(R.id.removeUser).isVisible = currentUserId != data?.user?.id

                //adding click listener
                popup.setOnMenuItemClickListener { p0 ->
                    when (p0?.itemId) {
                        R.id.removeUser -> {
                            onItemClick(RemoveReportedUser(data!!.id, data.user.id))
                        }

                        R.id.reports -> {
                            onItemClick(ReportedByMembers(data!!.id))
                        }
                    }

                    false
                }
                //displaying the popup
                popup.show()
            }
        }
    }

    private class SimplePostListDiffCallback : DiffUtil.ItemCallback<PostListResult>() {
        override fun areItemsTheSame(oldItem: PostListResult, newItem: PostListResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostListResult, newItem: PostListResult): Boolean {
            return oldItem == newItem
        }
    }
}