package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioSkillsCertificateBinding
import com.primapp.extensions.loadImageWithFitCenter
import com.primapp.model.portfolio.SkillsCertificateData
import javax.inject.Inject

class PortfolioSkillsNCertificateAdapter @Inject constructor() :
    RecyclerView.Adapter<PortfolioSkillsNCertificateAdapter.SkillsNCertifiacteViewHolder>() {

    val list = ArrayList<SkillsCertificateData>()

    fun addData(listData: ArrayList<SkillsCertificateData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsNCertifiacteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SkillsNCertifiacteViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_portfolio_skills_certificate,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SkillsNCertifiacteViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class SkillsNCertifiacteViewHolder(val binding: ItemPortfolioSkillsCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SkillsCertificateData) {
            binding.data = data
        }
    }
}
