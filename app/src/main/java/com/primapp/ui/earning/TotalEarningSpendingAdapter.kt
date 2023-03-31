package com.primapp.ui.earning

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemEarningSpendingLayoutBinding

class TotalEarningSpendingAdapter (private var context: Context, private var type: String, private var projectList: ArrayList<EarningSpendingData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        fun bind(projectData: EarningSpendingData) {
            binding.tvTitle.text = projectData.title
            binding.tvDate.text = projectData.date
            if(type == "Earning") {
                binding.tvAmount.text = "+$${projectData.amount}"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            else {
                binding.tvAmount.text = "-$${projectData.amount}"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        }
    }
}