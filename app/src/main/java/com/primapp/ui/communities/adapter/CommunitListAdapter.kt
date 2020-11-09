package com.primapp.ui.communities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListCommunityBinding
import javax.inject.Inject

class CommunitListAdapter @Inject constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<CommunitListAdapter.CommunityViewHolder>() {

    val psychicList = ArrayList<String>()

    fun addData(listData: List<String>) {
        psychicList.clear()
        psychicList.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommunityViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_list_community,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 13 //psychicList.size

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind()
    }

    inner class CommunityViewHolder(val binding: ItemListCommunityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
//            binding.psychicData = psychicListData
//            binding.cardPsychic.setOnClickListener {
//                onItemClick(psychicListData)
//            }
//            binding.tvChatWithPsychic.setOnClickListener {
//                onItemClick("kuch b")
//            }
        }
    }
}