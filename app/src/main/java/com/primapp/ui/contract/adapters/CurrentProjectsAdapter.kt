package com.primapp.ui.contract.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemCurrentProjectsLayoutBinding
import com.primapp.model.contract.ResultsItem
import javax.inject.Inject

class CurrentProjectsAdapter@Inject constructor(val onItemClick: (Int) -> Unit) :
    PagingDataAdapter<ResultsItem, CurrentProjectsAdapter.PostsViewHolder>(PostListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_current_projects_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            onItemClick(getItem(position)?.id?:0)
        }
    }

    inner class PostsViewHolder(val binding: ItemCurrentProjectsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResultsItem?) {
            binding.data = data
        }
    }

    private class PostListDiffCallback : DiffUtil.ItemCallback<ResultsItem>() {
        override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
            return oldItem == newItem
        }
    }
}