package com.primapp.ui.portfolio.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioBenefitsBinding
import com.primapp.model.portfolio.BenefitsData
import javax.inject.Inject

class PortfolioBenefitsAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<PortfolioBenefitsAdapter.BenefitsViewHolder>() {

    val list = ArrayList<BenefitsData>()

    fun addData(listData: ArrayList<BenefitsData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BenefitsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_benefits,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BenefitsViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class BenefitsViewHolder(val binding: ItemPortfolioBenefitsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BenefitsData, position: Int) {
            binding.data = data
            if (position % 2 == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.llBenefitsLayout.backgroundTintList =
                        ContextCompat.getColorStateList(binding.llBenefitsLayout.context, R.color.lightAccent)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.llBenefitsLayout.backgroundTintList =
                        ContextCompat.getColorStateList(binding.llBenefitsLayout.context, R.color.lightGrey)
                }
            }

            binding.llBenefitsLayout.setOnClickListener {
                onItemClick.invoke(data.name)
            }
        }
    }
}
