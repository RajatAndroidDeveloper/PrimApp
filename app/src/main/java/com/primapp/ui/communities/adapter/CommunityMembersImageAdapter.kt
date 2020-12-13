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

    val psychicList = ArrayList<String>()

    fun addData(listData: List<String>) {
        psychicList.clear()
        psychicList.addAll(listData)
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

    override fun getItemCount(): Int = 5 //psychicList.size

    override fun onBindViewHolder(holder: CommunityImageViewHolder, position: Int) {
        holder.bind()
    }

    inner class CommunityImageViewHolder(val binding: ItemListMemberImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.ivProfilePic.loadCircularImageWithBorder("https://static.toiimg.com/thumb/msid-73540687,width-800,height-600,resizemode-75,imgsize-575035,pt-32,y_pad-40/73540687.jpg")
        }
    }
}