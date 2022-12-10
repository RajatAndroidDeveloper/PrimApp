package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioExperienceBinding
import javax.inject.Inject

class PortfolioExperienceAdapter @Inject constructor() :
    RecyclerView.Adapter<PortfolioExperienceAdapter.ExperienceViewHolder>() {

    val list = ArrayList<String>()

    fun addData(listData: List<String>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExperienceViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_experience,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 3 //list.size

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.bind(position == 0, position == (itemCount - 1))
    }

    inner class ExperienceViewHolder(val binding: ItemPortfolioExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(isFirstItem: Boolean, isLastItem: Boolean) {
            //binding.data = data
            binding.ivTopDottedLine.isVisible = !isFirstItem
            binding.ivBottomDottedLine.isVisible = !isLastItem

            if(isFirstItem){
                binding.tvCompanyName.text = "Yapapp India Pvt. Ltd"
                binding.tvCompnyDetails.text = "2yrs 3months | Mohali"
            }

            if(isLastItem){
                binding.tvCompanyName.text = "Infosys Pvt ltd"
                binding.tvCompnyDetails.text = "1yrs 3months | Chandigarh"
            }

        }
    }
}
