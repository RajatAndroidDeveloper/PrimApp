package com.primapp.ui.portfolio.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioUpdateBenefitsBinding
import com.primapp.databinding.ItemPortfolioUpdateExperienceBinding
import com.primapp.model.DeleteItem
import com.primapp.model.EditBenefits
import com.primapp.model.EditExperience
import com.primapp.model.portfolio.BenefitsData
import com.primapp.model.portfolio.ExperienceData
import javax.inject.Inject

class UpdateExperienceAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<UpdateExperienceAdapter.UpdateExperienceViewHolder>() {

    val list = ArrayList<ExperienceData>()

    fun addData(listData: ArrayList<ExperienceData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun addItem(benefitsData: ExperienceData) {
        val lastIndex = this.list.size
        list.add(benefitsData)
        notifyItemInserted(lastIndex)
    }

    fun updateItem(itemData: ExperienceData?) {
        itemData?.let { data ->
            val dataInList = list.find { it.id == data.id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list[index] = data

            notifyItemChanged(index)
        }
    }

    fun deleteItem(id: Int?) {
        id?.let { id ->
            val dataInList = list.find { it.id == id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list.removeAt(index)

            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateExperienceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UpdateExperienceViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_update_experience,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UpdateExperienceViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class UpdateExperienceViewHolder(val binding: ItemPortfolioUpdateExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ExperienceData, position: Int) {
            binding.data = data

            binding.ivEdit.setOnClickListener {
                onItemClick(EditExperience(data))
            }

            binding.ivDelete.setOnClickListener {
                onItemClick(DeleteItem(data.id!!))
            }
        }
    }
}
