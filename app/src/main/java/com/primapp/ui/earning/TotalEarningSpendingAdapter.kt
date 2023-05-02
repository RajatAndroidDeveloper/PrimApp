package com.primapp.ui.earning

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.ItemEarningSpendingLayoutBinding
import com.primapp.model.earning.ContentItem
import com.primapp.utils.DateTimeUtils

class TotalEarningSpendingAdapter (private var projectList: ArrayList<ContentItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemEarningSpendingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrentProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = holder as CurrentProjectsViewHolder
        viewHolder.bind(projectList[position])
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    inner class CurrentProjectsViewHolder(val binding: ItemEarningSpendingLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(projectData: ContentItem) {
            binding.data = projectData
            binding.tvDate.text = DateTimeUtils.getDateAndTimeFromMillis(projectData.contract?.endDate, DateTimeUtils.STRING_DATE_FORMAT_TIME)
        }
    }
}