package com.primapp.ui.initial

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemSimpleTextBinding
import javax.inject.Inject
//
//class GenderListAdapter @Inject constructor(val onItemClick: (Any) -> Unit) :
//   ArrayAdapter<String> {
//
//    val list = ArrayList<String>()
//
//    fun addData(listData: List<String>) {
//        list.clear()
//        list.addAll(listData)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderListViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return GenderListViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_simple_text, parent, false))
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    override fun onBindViewHolder(holder: GenderListViewHolder, position: Int) {
//        holder.bind(list[position])
//    }
//
//    inner class GenderListViewHolder(val binding: ItemSimpleTextBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(data: String) {
//            binding.data = data
////            binding.root.setOnClickListener {
////                onItemClick(zodiacData)
////            }
//        }
//    }
//}