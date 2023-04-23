package com.primapp.ui.contract.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemOwnContractLayoutBinding
import com.primapp.model.mycontracts.OngoingContractsItem
import javax.inject.Inject

class OwnContractAdapter @Inject constructor(val onItemClick: (Int) -> Unit) :
    PagingDataAdapter<OngoingContractsItem, OwnContractAdapter.PostsViewHolder>(PostListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_own_contract_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            onItemClick(getItem(position)?.id ?: 0)
        }
    }

    inner class PostsViewHolder(val binding: ItemOwnContractLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OngoingContractsItem?) {
            binding.ongoingContract = data
            binding.contractType = "ongoing"
            binding.hideData = "no"
        }
    }

    private class PostListDiffCallback : DiffUtil.ItemCallback<OngoingContractsItem>() {
        override fun areItemsTheSame(oldItem: OngoingContractsItem, newItem: OngoingContractsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OngoingContractsItem, newItem: OngoingContractsItem): Boolean {
            return oldItem == newItem
        }
    }
}
