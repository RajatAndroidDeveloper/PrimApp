package com.primapp.ui.communities.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemCommunityMembersBinding
import com.primapp.model.members.CommunityMembersData
import javax.inject.Inject

class CommunityMembersListPagedAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<CommunityMembersData, CommunityMembersListPagedAdapter.CommunityMembersViewHolder>(
        CommunityMembersDiffCallback()
    ) {

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