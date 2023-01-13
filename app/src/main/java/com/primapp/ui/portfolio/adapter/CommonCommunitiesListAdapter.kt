package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.MentorshipStatusTypes
import com.primapp.databinding.ItemCommonCommunitesBinding
import com.primapp.model.RequestMentorWithCommunityId
import com.primapp.model.members.CommunityMembersData
import com.primapp.model.portfolio.CommonCommunites
import javax.inject.Inject

class CommonCommunitiesListAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<CommonCommunitiesListAdapter.CommonCommunitiesViewHolder>() {

    val list = ArrayList<CommonCommunites>()

    fun addData(listData: ArrayList<CommonCommunites>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun markRequestAsSent(communityId: Int) {
        val item: CommonCommunites? = list.find { it.id == communityId }
        item?.let {
            val position = list.indexOf(it)
            list[position].mentor_status = 2
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonCommunitiesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommonCommunitiesViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_common_communites,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommonCommunitiesViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class CommonCommunitiesViewHolder(val binding: ItemCommonCommunitesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonCommunites, position: Int) {
            binding.data = data

            binding.btnInviteMembers.setOnClickListener {
                onItemClick(RequestMentorWithCommunityId(data.id))
            }

            binding.btnInviteMembers.isVisible = (data.mentor_status == MentorshipStatusTypes.PENDING || data.mentor_status == MentorshipStatusTypes.CAN_SEND_REQUEST)
        }
    }
}