package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemPortfolioSkillsCertificateBinding
import com.primapp.extensions.loadImageWithFitCenter
import javax.inject.Inject

class PortfolioSkillsNCertificateAdapter @Inject constructor() :
    RecyclerView.Adapter<PortfolioSkillsNCertificateAdapter.SkillsNCertifiacteViewHolder>() {

    val list = ArrayList<String>()

    fun addData(listData: List<String>) {
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

    override fun getItemCount(): Int = 3 //list.size

    override fun onBindViewHolder(holder: SkillsNCertifiacteViewHolder, position: Int) {
        holder.bind(position == 0, position == (itemCount - 1))
    }

    inner class SkillsNCertifiacteViewHolder(val binding: ItemPortfolioSkillsCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(isFirstItem: Boolean, isLastItem: Boolean) {
            //binding.data = data
            binding.ivSkillIcon.loadImageWithFitCenter(binding.root.context, "https://mpng.subpng.com/20190328/xcl/kisspng-europython-logo-programming-language-portable-netw-join-our-team-job-opportunities-sample-solutions-5c9d90c3c63625.6121225015538300838119.jpg")
        }
    }
}
