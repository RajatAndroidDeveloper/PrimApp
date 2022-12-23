package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioAddRemoveBinding
import com.primapp.model.auth.ReferenceItems
import javax.inject.Inject

class PortfolioAddRemoveItemAdapter @Inject constructor() :
    RecyclerView.Adapter<PortfolioAddRemoveItemAdapter.AddRemoveItemViewHolder>() {

    val list = ArrayList<ReferenceItems>()

    fun addData(listData: ArrayList<ReferenceItems>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun addItem(data: ReferenceItems?) {
        data?.let {
            val lastIndex = list.size
            list.add(it)
            notifyItemInserted(lastIndex)
        }
    }

    fun removeItem(id: Int?) {
        id?.let { id ->
            val dataInList = list.find { it.itemId == id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list.removeAt(index)

            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRemoveItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AddRemoveItemViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_add_remove,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AddRemoveItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class AddRemoveItemViewHolder(val binding: ItemPortfolioAddRemoveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReferenceItems) {
            binding.data = data

            binding.ivRemove.setOnClickListener {
                removeItem(data.itemId)
            }
        }
    }
}
