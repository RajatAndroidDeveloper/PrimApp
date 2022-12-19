package com.primapp.ui.portfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.PostFileType
import com.primapp.databinding.ItemAddMentoringPortfolioBinding
import com.primapp.databinding.ItemAddMoreGridBinding
import com.primapp.model.DeleteItem
import com.primapp.model.DownloadFile
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.model.portfolio.MentoringPortfolioData
import javax.inject.Inject

class AddMentoringPortfolioAdapter @Inject constructor(val isLoggedInUser: Boolean, val onItemClick: (Any?) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val list = ArrayList<MentoringPortfolioData>()

    fun addData(listData: ArrayList<MentoringPortfolioData>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }

    fun addItem(data: MentoringPortfolioData) {
        val lastIndex = list.size
        list.add(data)
        notifyItemInserted(lastIndex)
    }

    fun removeItem(id: Int?) {
        id?.let { id ->
            val dataInList = list.find { it.id == id }
            val index = list.indexOf(dataInList)
            if (index != -1)
                list.removeAt(index)

            notifyItemRemoved(index)
        }
    }

    fun toggleRemoveButton() {
        list.forEach {
            it.isShowRemove = !it.isShowRemove
        }
        notifyItemRangeChanged(0, list.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) {
            TYPE_ADD_MORE_ITEM
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_ITEM -> {
                return AddMentoringViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_add_mentoring_portfolio,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return AddMoreItemHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_add_more_grid,
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun getItemCount(): Int {
        if (isLoggedInUser) {
            return list.size + 1
        } else {
            return list.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ITEM -> (holder as AddMentoringViewHolder).bind(list[position])
            TYPE_ADD_MORE_ITEM -> (holder as AddMoreItemHolder).bind()
        }
    }

    inner class AddMentoringViewHolder(val binding: ItemAddMentoringPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

            binding.ivRemove.setOnClickListener {
                onItemClick(DeleteItem(data.id))
            }
        }
    }

    inner class AddMoreItemHolder(val binding: ItemAddMoreGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.cardAddMore.setOnClickListener {
                onItemClick(null)
            }
        }
    }

    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_ADD_MORE_ITEM = 0
    }
}