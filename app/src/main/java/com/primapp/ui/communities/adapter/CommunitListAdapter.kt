package com.primapp.ui.communities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListCommunityBinding
import com.primapp.databinding.ItemListPostBinding
import com.primapp.extensions.loadImageWithProgress
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
                R.layout.item_list_post,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 5 //psychicList.size

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind()
    }

    inner class CommunityViewHolder(val binding: ItemListPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if(bindingAdapterPosition%2==0)
            binding.ivPostPreview.loadImageWithProgress(binding.root.context,"https://www.technipages.com/wp-content/uploads/2012/02/Adding-String-mod-iOS.png")
            else
            binding.ivPostPreview.loadImageWithProgress(binding.root.context,"https://www.text-image.com/samples/girl.jpg")

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