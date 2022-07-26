package com.primapp.ui.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemEmojiChatOptionsBinding
import com.primapp.model.chat.EmojiModel
import javax.inject.Inject

class EmojiOptionsAdapter @Inject constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<EmojiOptionsAdapter.EmojiOptionsViewHolder>() {

    val emojiList = ArrayList<EmojiModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun addData(listData: ArrayList<EmojiModel>) {
        emojiList.clear()
        emojiList.addAll(listData)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiOptionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EmojiOptionsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_emoji_chat_options,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = emojiList.size

    override fun onBindViewHolder(holder: EmojiOptionsViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    inner class EmojiOptionsViewHolder(val binding: ItemEmojiChatOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EmojiModel) {
            binding.data = data

            binding.ivEmoticon.setOnClickListener {
                onItemClick(data)
            }
        }
    }
}