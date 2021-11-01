package com.primapp.ui.post.reported_post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemReportedMembersBinding
import com.primapp.databinding.ItemSimplePostBinding
import com.primapp.model.post.ReportedMembers
import javax.inject.Inject

class ReportedMembersAdapter @Inject constructor() : RecyclerView.Adapter<ReportedMembersAdapter.ReportedMembersViewHolder>() {

    val list = ArrayList<ReportedMembers>()

    fun addData(listData: List<ReportedMembers>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportedMembersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReportedMembersViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_reported_members,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ReportedMembersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ReportedMembersViewHolder(val binding: ItemReportedMembersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReportedMembers) {
            binding.data = data
        }
    }
}
