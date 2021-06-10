package com.primapp.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemChatUserBinding
import com.primapp.databinding.ItemCommunityMembersBinding
import com.primapp.model.chat.ChatUser

class MentorMenteeChatAdapter constructor(val onItemClick: (Any?) -> Unit) :
    PagingDataAdapter<ChatUser, MentorMenteeChatAdapter.ChatUserViewHolder>(
        ChatUsersDiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatUserViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_chat_user,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ChatUserViewHolder(val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ChatUser) {
            binding.data = data

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    private class ChatUsersDiffCallback : DiffUtil.ItemCallback<ChatUser>() {
        override fun areItemsTheSame(oldItem: ChatUser, newItem: ChatUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatUser, newItem: ChatUser): Boolean {
            return oldItem == newItem
        }
    }
}