package com.primapp.ui.communities.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemListCommunityBinding
import com.primapp.model.category.CommunityData
import javax.inject.Inject

class CommunityPagedListAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<CommunityData, CommunityPagedListAdapter.CommunityViewHolder>(CommunityDiffCallback()) {

    // Set this fragment Type to make Join button -> Edit
    var fragmentType: String? = null

    fun markCommunityAsJoined(communityId: Int?) {
        val item: CommunityData? = snapshot().items.find { it.id == communityId }
        item?.let {
            val position = snapshot().items.indexOf(it)
            snapshot().items.get(position).isJoined = true
            notifyItemChanged(position)
        }
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

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class CommunityViewHolder(private val binding: ItemListCommunityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: CommunityData?) {
            binding.data = data
            binding.type = fragmentType
            binding.btnJoin.setOnClickListener {
                onItemClick(data?.id)
            }

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    private class CommunityDiffCallback : DiffUtil.ItemCallback<CommunityData>() {
        override fun areItemsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem == newItem
        }
    }
}