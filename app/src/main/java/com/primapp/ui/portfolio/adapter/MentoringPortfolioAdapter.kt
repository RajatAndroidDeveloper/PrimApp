package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemMentoringPortfolioBinding
import com.primapp.extensions.loadImageWithFitCenter
import com.primapp.model.DownloadFile
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.model.portfolio.MentoringPortfolioData
import javax.inject.Inject

class MentoringPortfolioAdapter @Inject constructor(val onItemClick: (Any?) -> Unit) : RecyclerView.Adapter<MentoringPortfolioAdapter.MentoringPortfolioViewHolder>() {

    val list = ArrayList<MentoringPortfolioData>()

    fun addData(listData: ArrayList<MentoringPortfolioData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentoringPortfolioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MentoringPortfolioViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_mentoring_portfolio,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MentoringPortfolioViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class MentoringPortfolioViewHolder(val binding: ItemMentoringPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MentoringPortfolioData) {
            binding.data = data

            binding.cardMentoringPortFolio.setOnClickListener {
                when (data.fileType) {
                    PostFileType.VIDEO -> {
                        onItemClick(ShowVideo(data.imageUrl.toString()))
                    }
                    PostFileType.IMAGE -> {
                        onItemClick(ShowImage(data.imageUrl.toString()))
                    }
                    PostFileType.GIF -> {
                        onItemClick(ShowImage(data.imageUrl.toString()))
                    }
                    PostFileType.FILE -> {
                        onItemClick(DownloadFile(data.imageUrl.toString()))
                    }
                }
            }
        }
    }
}
