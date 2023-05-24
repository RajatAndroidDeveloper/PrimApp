package com.primapp.ui.contract.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.ItemAcceptRequestLayoutBinding
import com.primapp.model.contract.AcceptedByItem
import com.primapp.utils.DateTimeUtils

class AcceptRequestAdapter (private val acceptedByRequestList : ArrayList<AcceptedByItem>, private var context: Context, private var onButtonCLickCallback: OnButtonClickCallback): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val loadMoreBinding = ItemAcceptRequestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmendRequestViewHolder(loadMoreBinding)
    }

    override fun getItemCount(): Int {
        return acceptedByRequestList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: AmendRequestViewHolder = holder as AmendRequestViewHolder
        viewHolder.bindData(acceptedByRequestList[position])
    }

    inner class AmendRequestViewHolder(val binding: ItemAcceptRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(data: AcceptedByItem){
            binding.acceptedBy = data
        }
    }
}
