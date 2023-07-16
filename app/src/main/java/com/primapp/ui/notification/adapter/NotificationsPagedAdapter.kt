package com.primapp.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.NotificationTypes
import com.primapp.constants.NotificationViewTypes
import com.primapp.databinding.*
import com.primapp.model.*
import com.primapp.model.notification.NotificationResult
import com.primapp.model.notification.NotificationUIModel
import javax.inject.Inject

class NotificationsPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<NotificationUIModel, RecyclerView.ViewHolder>(NotificationDiffCallback()) {

    fun updateRequestAsAccepted(dataId: Int?) {
        if (dataId == null) return
        val item: NotificationUIModel? =
            snapshot().items.find { it is NotificationUIModel.NotificationItem && it.notification.dataId == dataId }

        item?.let {
            val position = snapshot().items.indexOf(it)
            (snapshot().items.get(position) as NotificationUIModel.NotificationItem).notification.notificationType =
                NotificationTypes.MENTORSHIP_REQUEST_ACTION
            (snapshot().items.get(position) as NotificationUIModel.NotificationItem).notification.title = "accepted"
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val viewType: NotificationUIModel? = getItem(position)
        return when (viewType) {
            is NotificationUIModel.NotificationItem -> {
                when (viewType.notification.notificationType) {
                    NotificationTypes.MENTORSHIP_REQUEST -> {
                        NotificationViewTypes.MENTORSHIP_REQUEST_VIEW
                    }
                    NotificationTypes.MENTORSHIP_UPDATE, NotificationTypes.MENTORSHIP_REQUEST_ACTION,
                    NotificationTypes.COMMUNITY_NOTIFICATION, NotificationTypes.ADMIN_ACTION_NOTIFICATION, NotificationTypes.CONTRACT_NOTIFICATION -> {
                        NotificationViewTypes.MENTORSHIP_UPDATE_VIEW
                    }
                    NotificationTypes.POST_RELATED_NOTIFICATION,  NotificationTypes.POST_VIRUS_DETECTED_NOTIFICATION-> {
                        NotificationViewTypes.POST_NOTIFICATION_VIEW
                    }
                    else -> {
                        NotificationViewTypes.OTHERS_VIEW
                    }
                }
            }

            is NotificationUIModel.SeparatorItem -> {
                NotificationViewTypes.SEPARATOR_VIEW
            }

            null -> throw UnsupportedOperationException("Unknown view")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            NotificationViewTypes.MENTORSHIP_REQUEST_VIEW -> {
                return MentorsRequestViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_mentor_request,
                        parent,
                        false
                    )
                )
            }
            NotificationViewTypes.MENTORSHIP_UPDATE_VIEW -> {
                return MentorsRequestUpdateViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_notification_normal,
                        parent,
                        false
                    )
                )
            }
            NotificationViewTypes.POST_NOTIFICATION_VIEW -> {
                return PostNotificationViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_notification_post,
                        parent,
                        false
                    )
                )
            }
            NotificationViewTypes.SEPARATOR_VIEW -> {
                return SeparatorViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_notification_separator,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return OthersViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_simple_text,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        when (uiModel) {
            is NotificationUIModel.NotificationItem -> {
                when (getItemViewType(position)) {
                    NotificationViewTypes.MENTORSHIP_REQUEST_VIEW -> {
                        (holder as MentorsRequestViewHolder).bindView(uiModel.notification)
                    }
                    NotificationViewTypes.MENTORSHIP_UPDATE_VIEW -> {
                        (holder as MentorsRequestUpdateViewHolder).bindView(uiModel.notification)
                    }
                    NotificationViewTypes.POST_NOTIFICATION_VIEW -> {
                        (holder as PostNotificationViewHolder).bindView(uiModel.notification)
                    }
                    else -> {
                        (holder as OthersViewHolder).bindView(uiModel.notification)
                    }
                }
            }
            is NotificationUIModel.SeparatorItem -> {
                (holder as SeparatorViewHolder).bindView(uiModel.description)
            }
        }

    }

    inner class MentorsRequestViewHolder(private val binding: ItemMentorRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: NotificationResult?) {
            binding.data = data

            binding.btnAccept.setOnClickListener {
                onItemClick(AcceptMetorRequest(data!!.dataId))
            }

            binding.btnReject.setOnClickListener {
                onItemClick(RejectMetorRequest(data!!.dataId))
            }

            binding.ivProfilePic.setOnClickListener {
                data?.sender?.let {
                    onItemClick(ShowUserProfile(it.id))
                }
            }

            binding.clNotificationMentorRequest.setOnClickListener {
                data?.community?.let {
                    onItemClick(ShowCommunityDetails(it))
                }
            }
        }
    }

    inner class MentorsRequestUpdateViewHolder(private val binding: ItemNotificationNormalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: NotificationResult?) {
            binding.data = data

            binding.ivProfilePic.setOnClickListener {
                data?.sender?.let {
                    onItemClick(ShowUserProfile(it.id))
                }
            }

            binding.clNotificationNormal.setOnClickListener {
                data?.community?.let {
                    onItemClick(ShowCommunityDetails(it))
                }
            }

            if (data?.contractDetails != null)
                binding.clNotificationNormal.setOnClickListener {
                    onItemClick(ShowContractDetails(data?.contractDetails?.id?:0))
                }
        }
    }

    inner class PostNotificationViewHolder(private val binding: ItemNotificationPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: NotificationResult?) {
            binding.data = data

            binding.ivProfilePic.setOnClickListener {
                data?.sender?.let {
                    onItemClick(ShowUserProfile(it.id))
                }
            }

            binding.clNotificationPost.setOnClickListener {
                if (data?.notificationType == NotificationTypes.POST_VIRUS_DETECTED_NOTIFICATION){
                    data?.postData?.let{
                        onItemClick(EditUploadedPost(data.postData.id, data.community.id))
                    }
                }else {
                    data?.community?.let {
                        onItemClick(ShowPostDetails(it.id, data.postData!!.id))
                    }
                }
            }
        }
    }

    inner class SeparatorViewHolder(private val binding: ItemNotificationSeparatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: String?) {
            binding.data = "$data"
        }
    }

    inner class OthersViewHolder(private val binding: ItemSimpleTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: NotificationResult?) {
            val param = itemView.layoutParams as RecyclerView.LayoutParams
            binding.root.isVisible = false
            param.height = 0
            param.width = 0
            binding.data = "Other View Type : Not implemented yet!!"
        }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationUIModel>() {
        override fun areItemsTheSame(oldItem: NotificationUIModel, newItem: NotificationUIModel): Boolean {
            return (oldItem is NotificationUIModel.NotificationItem && newItem is NotificationUIModel.NotificationItem &&
                    oldItem.notification.dataId == newItem.notification.dataId) || (oldItem is NotificationUIModel.SeparatorItem &&
                    newItem is NotificationUIModel.SeparatorItem && oldItem.description == newItem.description)
        }

        override fun areContentsTheSame(oldItem: NotificationUIModel, newItem: NotificationUIModel): Boolean {
            return oldItem == newItem
        }
    }
}