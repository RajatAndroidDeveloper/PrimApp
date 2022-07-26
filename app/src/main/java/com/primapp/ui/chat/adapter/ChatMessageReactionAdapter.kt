package com.primapp.ui.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemMessageReactionBinding
import com.sendbird.android.Reaction
import javax.inject.Inject

class ChatMessageReactionAdapter : RecyclerView.Adapter<ChatMessageReactionAdapter.ReactionViewHolder>() {

    val emojiList = ArrayList<Reaction>()

    @SuppressLint("NotifyDataSetChanged")
    fun addData(listData: ArrayList<Reaction>) {
        emojiList.clear()
        emojiList.addAll(listData)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReactionViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_message_reaction,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = if(emojiList.size > 3) 3 else emojiList.size

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    inner class ReactionViewHolder(val binding: ItemMessageReactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Reaction) {
            binding.data = data
        }
    }
}