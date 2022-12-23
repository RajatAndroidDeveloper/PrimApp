package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioUpdateSkillsBinding
import com.primapp.model.DeleteItem
import com.primapp.model.portfolio.SkillsCertificateData
import javax.inject.Inject

class UpdateSkillsAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<UpdateSkillsAdapter.UpdateSkillsViewHolder>() {

    val list = ArrayList<SkillsCertificateData>()

    fun addData(listData: ArrayList<SkillsCertificateData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun addItem(item: SkillsCertificateData) {
        val lastIndex = this.list.size
        list.add(item)
        notifyItemInserted(lastIndex)
    }

    fun addItems(list: ArrayList<SkillsCertificateData>) {
        val lastIndex = this.list.size
        this.list.addAll(list)
        notifyItemRangeChanged(lastIndex, list.size)
    }

    fun deleteItem(itemId: Int?) {
        itemId?.let { id ->
            val dataInList = list.find { it.id == id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list.removeAt(index)

            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateSkillsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UpdateSkillsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_update_skills,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UpdateSkillsViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class UpdateSkillsViewHolder(val binding: ItemPortfolioUpdateSkillsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SkillsCertificateData, position: Int) {
            binding.data = data

            binding.ivDelete.setOnClickListener {
                onItemClick(DeleteItem(data.id!!))
            }
        }
    }
}
