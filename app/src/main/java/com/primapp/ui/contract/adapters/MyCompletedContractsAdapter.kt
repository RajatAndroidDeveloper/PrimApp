package com.primapp.ui.contract.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemOwnContractLayoutBinding
import com.primapp.model.mycontracts.CompletedContractsItem
import kotlinx.android.synthetic.main.item_own_contract_layout.view.*
import javax.inject.Inject

class MyCompletedContractsAdapter  @Inject constructor(val onItemClick: (Int) -> Unit) :
    PagingDataAdapter<CompletedContractsItem, MyCompletedContractsAdapter.PostsViewHolder>(PostListDiffCallback()) {
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
        fun bind(data: CompletedContractsItem?) {
            binding.completedContract = data
            binding.contractType = "completed"
            binding.hideData = "no"
        }
    }

    private class PostListDiffCallback : DiffUtil.ItemCallback<CompletedContractsItem>() {
        override fun areItemsTheSame(oldItem: CompletedContractsItem, newItem: CompletedContractsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CompletedContractsItem, newItem: CompletedContractsItem): Boolean {
            return oldItem == newItem
        }
    }
}
