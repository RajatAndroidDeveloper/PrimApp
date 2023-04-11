package com.primapp.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.primapp.databinding.ItemMentorsAndMenteesBinding
import com.primapp.databinding.ItemMentorsAndMenteesLoadMoreBinding
import com.primapp.model.members.CommunityMembersData
import com.primapp.model.mentormentee.ResultsItem


class MentorsMenteesAdaptor(private var mentorsMenteesList: ArrayList<ResultsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface Callbacks {
        fun onClickLoadMore()
    }

    private var mCallbacks: Callbacks? = null
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    private var mWithHeader = false
    private var mWithFooter = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOOTER) {
            val loadMoreBinding = ItemMentorsAndMenteesLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadMoreViewHolder(loadMoreBinding)
        } else {
            val binding = ItemMentorsAndMenteesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MentorsMenteesViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadMoreViewHolder) {
            val loadMoreViewHolder: LoadMoreViewHolder = holder as LoadMoreViewHolder
            loadMoreViewHolder.itemView.setOnClickListener(View.OnClickListener { mCallbacks?.onClickLoadMore() })
        } else {
            val elementsViewHolder: MentorsMenteesViewHolder = holder as MentorsMenteesViewHolder
            val elements: ResultsItem = mentorsMenteesList!![position]
            elementsViewHolder.bind(elements)
        }
    }

    override fun getItemCount(): Int {
        var itemCount = mentorsMenteesList!!.size
        if (mWithHeader) itemCount++
        if (mWithFooter) itemCount++
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (mWithHeader && isPositionHeader(position)) return TYPE_HEADER
        return if (mWithFooter && isPositionFooter(position)) TYPE_FOOTER else TYPE_ITEM
    }
    
    fun isPositionHeader(position: Int): Boolean {
        return position == 0 && mWithHeader
    }

    fun isPositionFooter(position: Int): Boolean {
        return position == getItemCount() - 1 && mWithFooter
    }

    fun setWithHeader(value: Boolean) {
        mWithHeader = value
    }

    fun setWithFooter(value: Boolean) {
        mWithFooter = value
    }

    fun setCallback(callbacks: Callbacks) {
        mCallbacks = callbacks
    }

    inner class MentorsMenteesViewHolder(val binding: ItemMentorsAndMenteesBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResultsItem) {
            binding.data = data
        }
    }

    inner class LoadMoreViewHolder(itemView: ItemMentorsAndMenteesLoadMoreBinding) : RecyclerView.ViewHolder(itemView.root)
}