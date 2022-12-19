package com.primapp.ui.portfolio.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioUpdateBenefitsBinding
import com.primapp.model.DeleteItem
import com.primapp.model.EditBenefits
import com.primapp.model.portfolio.BenefitsData
import javax.inject.Inject

class AddBenefitsAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<AddBenefitsAdapter.UpdateBenefitsViewHolder>() {

    val list = ArrayList<BenefitsData>()

    fun addData(listData: ArrayList<BenefitsData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun addBenefit(benefitsData: BenefitsData) {
        val lastIndex = this.list.size
        list.add(benefitsData)
        notifyItemInserted(lastIndex)
    }

    fun updateBenefit(benefitsData: BenefitsData?) {
        benefitsData?.let { data ->
            val dataInList = list.find { it.id == data.id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list[index] = benefitsData

            notifyItemChanged(index)
        }
    }

    fun deleteBenefit(benefitId: Int?) {
        benefitId?.let { id ->
            val dataInList = list.find { it.id == id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list.removeAt(index)

            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateBenefitsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UpdateBenefitsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_update_benefits,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UpdateBenefitsViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class UpdateBenefitsViewHolder(val binding: ItemPortfolioUpdateBenefitsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BenefitsData, position: Int) {
            binding.data = data

            binding.ivEdit.setOnClickListener {
                onItemClick(EditBenefits(data))
            }

            binding.ivDelete.setOnClickListener {
                onItemClick(DeleteItem(data.id!!))
            }
        }
    }
}
