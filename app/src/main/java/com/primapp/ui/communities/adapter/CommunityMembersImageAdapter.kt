package com.primapp.ui.communities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListMemberImagesBinding
import com.primapp.extensions.loadCircularImageWithBorder
import javax.inject.Inject

class CommunityMembersImageAdapter @Inject constructor() :
    RecyclerView.Adapter<CommunityMembersImageAdapter.CommunityImageViewHolder>() {

    val list = ArrayList<String>()

    fun addData(listData: List<String>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommunityImageViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_list_member_images,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommunityImageViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class CommunityImageViewHolder(val binding: ItemListMemberImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.ivProfilePic.loadCircularImageWithBorder(url)
        }
    }
}