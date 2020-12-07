package com.primapp.ui.communities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListParentCategoryBinding
import com.primapp.model.category.CommunityData
import com.primapp.model.category.ParentCategoryResult
import javax.inject.Inject


class ParentCategoryListAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<ParentCategoryResult, ParentCategoryListAdapter.ParentCategoryViewHolder>(
        ParentCategoryDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentCategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ParentCategoryViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_list_parent_category,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ParentCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ParentCategoryViewHolder(val binding: ItemListParentCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parentCategory: ParentCategoryResult?) {
            binding.data = parentCategory
            binding.ivParentCategory.setOnClickListener {
                onItemClick(parentCategory)
            }
        }
    }

    private class ParentCategoryDiffCallback : DiffUtil.ItemCallback<ParentCategoryResult>() {
        override fun areItemsTheSame(oldItem: ParentCategoryResult, newItem: ParentCategoryResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ParentCategoryResult, newItem: ParentCategoryResult): Boolean {
            return oldItem == newItem
        }
    }
}
