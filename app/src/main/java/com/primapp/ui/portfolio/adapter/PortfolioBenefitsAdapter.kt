package com.primapp.ui.portfolio.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioBenefitsBinding
import javax.inject.Inject

class PortfolioBenefitsAdapter @Inject constructor() :
    RecyclerView.Adapter<PortfolioBenefitsAdapter.BenefitsViewHolder>() {

    val list = ArrayList<String>()

    fun addData(listData: List<String>) {
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

    override fun getItemCount(): Int = 12 //list.size

    override fun onBindViewHolder(holder: BenefitsViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class BenefitsViewHolder(val binding: ItemPortfolioBenefitsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            //binding.data = data
            if (position % 2 == 0) {
                // binding.llBenefitsLayout.setBac
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

            if (position == 2) {
                binding.tvSkillName.text = "Career Growth"
            }

            if (position == 3) {
                binding.tvSkillName.text = "Free"
            }

            if (position == 4) {
                binding.tvSkillName.text = "Higher Compensation"
            }
        }
    }
}
