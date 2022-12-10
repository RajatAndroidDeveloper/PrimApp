package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemMentoringPortfolioBinding
import com.primapp.extensions.loadImageWithFitCenter
import javax.inject.Inject

class MentoringPortfolioAdapter @Inject constructor() : RecyclerView.Adapter<MentoringPortfolioAdapter.MentoringPortfolioViewHolder>() {

    val list = ArrayList<String>()

    fun addData(listData: List<String>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentoringPortfolioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MentoringPortfolioViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_mentoring_portfolio,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 3 //list.size

    override fun onBindViewHolder(holder: MentoringPortfolioViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class MentoringPortfolioViewHolder(val binding: ItemMentoringPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            //binding.data = data
            if (position % 2 == 0) {
                binding.ivPortfolioPreview.loadImageWithFitCenter(binding.root.context, "https://picsum.photos/seed/picsum/536/354")
            }else{
                binding.ivPortfolioPreview.loadImageWithFitCenter(binding.root.context, "https://picsum.photos/id/237/536/354")
            }
        }
    }
}
