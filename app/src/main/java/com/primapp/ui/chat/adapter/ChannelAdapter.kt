package com.primapp.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.ItemChannelListBinding
import com.sendbird.android.channel.GroupChannel
import javax.inject.Inject

class ChannelListAdapter @Inject constructor(val onItemClick: (Any) -> Unit) : RecyclerView.Adapter<ChannelListAdapter.ChannelHolder>() {

    private var channels: MutableList<GroupChannel>

    init {
        channels = ArrayList()
    }

    fun setChannels(channels: MutableList<GroupChannel>) {
        this.channels = channels
        notifyDataSetChanged()
    }

    fun addChannels(channels: List<GroupChannel>?) {
        val lastIndex = this.channels.size
        this.channels.addAll(channels!!)
        notifyItemRangeInserted(lastIndex - 1, channels.size - 1)
    }

    fun updateChannel(groupChannel: GroupChannel?) {
        groupChannel?.let{ channel->
           val channelInList = this.channels.find { it.url == channel.url }
           val index = channels.indexOf(channelInList)
           if (index != -1)
               channels[index] = groupChannel

           notifyItemChanged(index)
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChannelHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_channel_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = channels.size

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bind(channels[position])
    }

    inner class ChannelHolder(val binding: ItemChannelListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupChannel: GroupChannel) {
            binding.channel = groupChannel
            binding.root.setOnClickListener {
                onItemClick(groupChannel)
            }
        }
    }
}