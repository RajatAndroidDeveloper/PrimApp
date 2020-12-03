package com.primapp.ui.communities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListParentCategoryBinding
import com.primapp.model.category.ParentCategoryResult
import javax.inject.Inject


class ParentCategoryListAdapter @Inject constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<ParentCategoryListAdapter.ParentCategoryViewHolder>() {

    val psychicList = ArrayList<ParentCategoryResult>()

    fun addData(listData: List<ParentCategoryResult>) {
        psychicList.clear()
        psychicList.addAll(listData)
        notifyDataSetChanged()
    }

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

    override fun getItemCount(): Int = psychicList.size

    override fun onBindViewHolder(holder: ParentCategoryViewHolder, position: Int) {
        holder.bind(psychicList[position])
    }

    inner class ParentCategoryViewHolder(val binding: ItemListParentCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parentCategory: ParentCategoryResult) {
            binding.data = parentCategory
            binding.ivParentCategory.setOnClickListener {
                onItemClick(parentCategory)
            }
        }
    }
}
