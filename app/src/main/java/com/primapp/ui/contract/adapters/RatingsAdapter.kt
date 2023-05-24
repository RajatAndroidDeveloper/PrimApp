package com.primapp.ui.contract.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.RatingListingItemLayoutBinding
import com.primapp.model.contract.RatingItem
import com.primapp.model.rating.ContentItem

class RatingsAdapter(private val ratingsList: ArrayList<ContentItem>, private val ratingList: ArrayList<RatingItem>?, private val type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val loadMoreBinding = RatingListingItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingsViewHolder(loadMoreBinding)
    }

    override fun getItemCount(): Int {
        return if(type == "ProjectDetails") ratingList!!.size else  ratingsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: RatingsViewHolder = holder as RatingsViewHolder
        if (type == "ProjectDetails"){
            viewHolder.bindRatingData(ratingList!![position])
        }else {
            viewHolder.bindData(ratingsList[position])
        }
    }

    inner class RatingsViewHolder(val binding: RatingListingItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ContentItem){
            binding.type = type
            binding.data = data
        }
        fun bindRatingData(data: RatingItem){
            binding.type = type
            binding.ratingData = data
        }
    }
}
