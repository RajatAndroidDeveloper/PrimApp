package com.primapp.ui.contract.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.RatingListingItemLayoutBinding
import com.primapp.model.rating.ContentItem
import com.primapp.utils.DateTimeUtils

class RatingsAdapter(private val ratingsList: ArrayList<ContentItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val loadMoreBinding = RatingListingItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingsViewHolder(loadMoreBinding)
    }

    override fun getItemCount(): Int {
        return ratingsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: RatingsViewHolder = holder as RatingsViewHolder
        viewHolder.bindData(ratingsList[position])
    }

    inner class RatingsViewHolder(val binding: RatingListingItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ContentItem){
            binding.data = data
        }
    }
}
