package com.primapp.ui.contract.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemOwnContractLayoutBinding
import com.primapp.model.mycontracts.CompletedContractsItem
import com.primapp.model.mycontracts.OngoingContractsItem

class MyContractWithoutFilterAdapter(
    private val context: Context,
    private val ongoingContractsList: ArrayList<OngoingContractsItem>,
    private val completedContractList: ArrayList<CompletedContractsItem>,
    private val contractType: String,
    private val onContractClickEvent: OnContractClickEvent
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val loadMoreBinding = ItemOwnContractLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyContractViewHolder(loadMoreBinding)
    }

    override fun getItemCount(): Int {
        return if (contractType == "ongoing") ongoingContractsList.size else completedContractList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: MyContractViewHolder = holder as MyContractViewHolder
        if (contractType == "ongoing") viewHolder.bindOngoingData(ongoingContractsList[position]) else viewHolder.bindCompletedData(
            completedContractList[position]
        )

        viewHolder.itemView.setOnClickListener {
            if (contractType == "ongoing") {
                onContractClickEvent.onContractItemClickEvent(contractType, ongoingContractsList[position].id ?: 0)
            } else
                onContractClickEvent.onContractItemClickEvent(contractType, completedContractList[position].id ?: 0)
        }

        if (contractType == "completed") {
            viewHolder.binding.tvSeeDetails.isVisible = true
        }
    }

    inner class MyContractViewHolder(val binding: ItemOwnContractLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindOngoingData(data: OngoingContractsItem) {
            binding.ongoingContract = data
            binding.contractType = contractType
            binding.hideData = "no"
        }

        fun bindCompletedData(data: CompletedContractsItem) {
            binding.completedContract = data
            binding.contractType = contractType
            binding.hideData = "yes"
        }
    }
}

interface OnContractClickEvent {
    fun onContractItemClickEvent(contractType: String, contractId: Int)
}