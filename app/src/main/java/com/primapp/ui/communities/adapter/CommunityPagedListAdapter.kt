package com.primapp.ui.communities.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.ItemListCommunityBinding
import com.primapp.model.category.CommunityData

class CommunityPagedListAdapter :
    PagingDataAdapter<CommunityData, CommunityPagedListAdapter.CommunityViewHolder>(CommunityDiffCallback()) {

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        return CommunityViewHolder(
            ItemListCommunityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    inner class CommunityViewHolder(private val binding: ItemListCommunityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: CommunityData?) {
            binding.data = data
        }
    }

    private class CommunityDiffCallback : DiffUtil.ItemCallback<CommunityData>() {
        override fun areItemsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem == newItem
        }
    }
}