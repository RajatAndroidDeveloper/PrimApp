package com.primapp.ui.post.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemListPostBinding
import com.primapp.extensions.setAllOnClickListener
import com.primapp.model.*
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.ApiConstant
import javax.inject.Inject


class PostListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit, ) :
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

    fun addPostToBookmark(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isBookmark = true
            notifyItemChanged(position)
        }
    }

    fun getUpdateMuteStatus(state: Boolean, position: Int) {
        snapshot().items[position].videoMute= state
        notifyItemChanged(position)
    }

    fun getMutedStatus(position: Int): Boolean {
        val item = snapshot().items[position]
        return item.videoMute
    }

    fun removePostAsBookmarked(postId: Int?) {
        val item: PostListResult? = snapshot().items.find { it.id == postId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isBookmark = false
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

    fun getPostThumbnailUrl(position: Int): String{
        val item = snapshot().items[position]
        return item.getThumbnailUrl?:""
    }

    fun getPostUrl(position: Int): String{
        val item = snapshot().items[position]
        return item.imageUrl?:""
    }

    fun getPostType(position: Int): String{
        val item = snapshot().items[position]
        return item.fileType?:""
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
            //Reest cover image on reload
            binding.llCoverImage.isVisible = false

            if (data!!.fileType == PostFileType.FILE) {
                if (data.postContentFile?.contains("/") == true)
                    binding.tvFileName.text = data.postContentFile.toString().split("/")[1]
                else
                    binding.tvFileName.text = data.postContentFile
            }

            if(data!!.fileType == PostFileType.VIDEO) {
                binding.ivMuteVideo.visibility = View.VISIBLE
                binding.ivPostPreview.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
            } else{
                binding.ivMuteVideo.visibility = View.GONE
                binding.ivPostPreview.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
            }

            binding.ivMuteVideo.setOnClickListener{
                onItemClick(MuteVideo(position))
            }

            binding.clPostAttachment.setOnClickListener {
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

            binding.tvViewContent.setOnClickListener {
                binding.clPostAttachmentInappropriateContent.isVisible = false
                binding.llCoverImage.isVisible = true
            }

            binding.llCoverImage.setOnClickListener {
                binding.clPostAttachmentInappropriateContent.isVisible = true
                binding.llCoverImage.isVisible = false
            }

            binding.tvSeeWhy.setOnClickListener {
                onItemClick(LoadWebUrl(ApiConstant.SENSITIVE_DATA_DISCLAIMER))
            }

            binding.ivLike.setOnClickListener {
                data?.let {
                    onItemClick(LikePost(it))
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

            binding.tvLikesCount.setOnClickListener {
                data?.let {
                    onItemClick(LikePostMembers(it))
                }
            }

            binding.groupProfileInfo.setAllOnClickListener(View.OnClickListener {
                data?.let {
                    onItemClick(ShowUserProfile(it.user.id))
                }
            })

            binding.ivBookmark.setOnClickListener {
                data?.let {
                    onItemClick(BookmarkPost(it))
                }
            }

            binding.ivShare.setOnClickListener {
                data?.let {
                    onItemClick(SharePost(it, binding.clPostToShare))
                }
            }

            binding.ivMore.setOnClickListener {
                //creating a popup menu
                val popup = PopupMenu(binding.root.context, binding.ivMore)
                //inflating menu from xml resource
                if (data!!.isCreatedByMe) {
                    popup.inflate(R.menu.post_edit_menu)
                } else {
                    if (data.isHidden) {
                        popup.inflate(R.menu.post_unhide_menu)
                    } else {
                        popup.inflate(R.menu.post_report_menu)
                    }
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

                            R.id.unHidePost -> {
                                onItemClick(UnHidePost(data))
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