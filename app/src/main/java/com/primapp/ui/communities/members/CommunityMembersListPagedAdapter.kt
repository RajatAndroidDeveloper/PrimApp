package com.primapp.ui.communities.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.MentorshipStatusTypes
import com.primapp.databinding.ItemCommunityMembersBinding
import com.primapp.model.RequestMentor
import com.primapp.model.ShowImage
import com.primapp.model.ShowUserProfile
import com.primapp.model.members.CommunityMembersData
import javax.inject.Inject

class CommunityMembersListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<CommunityMembersData, CommunityMembersListPagedAdapter.CommunityMembersViewHolder>(
        CommunityMembersDiffCallback()
    ) {

    private var listViewType: String? = null
    private var otherUserProfile: Boolean = false

    fun markRequestAsSent(mentorId: Int) {
        val item: CommunityMembersData? = snapshot().items.find { it.user.id == mentorId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items[position].user.mentor_status = 2
            notifyItemChanged(position)
        }
    }

    fun removeMember(requestId: Int?) {
        val item: CommunityMembersData? = snapshot().items.find { it.id == requestId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            notifyItemRemoved(position)
        }
    }

    fun setViewType(type: String, isOtherProfile: Boolean) {
        listViewType = type
        otherUserProfile = isOtherProfile
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityMembersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommunityMembersViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_community_members,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommunityMembersViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class CommunityMembersViewHolder(val binding: ItemCommunityMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommunityMembersData) {
            binding.data = data

            binding.btnInviteMembers.setOnClickListener {
                onItemClick(RequestMentor(data))
            }

            binding.llMemberDetails.setOnClickListener {
                onItemClick(ShowUserProfile(data.user.id))
            }

            binding.ivProfilePic.setOnClickListener {
                onItemClick(ShowImage(data.user.getImageUrl))
            }

            // Show community name instead of username in case of mentor/mentee list
            if (listViewType == CommunityMembersFragment.MENTOR_MEMBERS_LIST ||
                listViewType == CommunityMembersFragment.MENTEE_MEMBERS_LIST
            ) {
                binding.tvUsername.isVisible = false
                binding.tvCommunityName.isVisible = true
            } else {
                binding.tvUsername.isVisible = true
                binding.tvCommunityName.isVisible = false
            }

            if (listViewType == CommunityMembersFragment.MENTEE_MEMBERS_LIST && !otherUserProfile) {
                //Show end mentorship button
                binding.btnInviteMembers.isVisible = true
            } else {
                binding.btnInviteMembers.isVisible =
                    data.community?.isJoined == true && (data.user.mentor_status == MentorshipStatusTypes.PENDING || data.user.mentor_status == MentorshipStatusTypes.CAN_SEND_REQUEST)

            }
        }
    }

    private class CommunityMembersDiffCallback : DiffUtil.ItemCallback<CommunityMembersData>() {
        override fun areItemsTheSame(oldItem: CommunityMembersData, newItem: CommunityMembersData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommunityMembersData, newItem: CommunityMembersData): Boolean {
            return oldItem == newItem
        }
    }
}