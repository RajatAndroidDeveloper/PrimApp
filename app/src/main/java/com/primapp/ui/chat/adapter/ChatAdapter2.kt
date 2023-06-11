package com.primapp.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.*
import com.primapp.utils.DateTimeUtils
import com.sendbird.android.*
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.handler.CompletionHandler
import com.sendbird.android.message.AdminMessage
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.FileMessage
import com.sendbird.android.message.UserMessage
import com.sendbird.android.params.MessageListParams
import com.sendbird.android.user.User

class ChatAdapter2 constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mIsMessageListLoading = false
    var channel: GroupChannel? = null

    val messageList: ArrayList<BaseMessage> = arrayListOf()

    fun addData(listData: List<BaseMessage>) {
        messageList.clear()
        messageList.addAll(listData)
        notifyDataSetChanged()
        notifyItemInserted(messageList.size - 1)
    }

    fun addFirst(message: BaseMessage) {
        messageList.add( message)
        notifyItemInserted(messageList.size - 1)
    }

    fun addMessages(messages: List<BaseMessage>) {
//        messageList.addAll(0, messages)
//        notifyItemRangeInserted(0, messageList.size - 1)
        val lastIndex = this.messageList.size
        this.messageList.addAll(messages)
        notifyItemRangeInserted(lastIndex, messageList.size)
    }

    fun updateMessage(message: BaseMessage) {
        val messageInList = messageList.find { it.requestId == message.requestId }
        val index = messageList.indexOf(messageInList)
        if (index != -1)
            messageList[index] = message

        notifyItemChanged(index)
    }

    override fun getItemViewType(position: Int): Int {
        val message: BaseMessage = messageList[position]
        return when (message) {
            is UserMessage -> {
                if (message.sender?.userId == SendbirdChat.currentUser?.userId) {
                    VIEW_TYPE_USER_MESSAGE_ME
                } else {
                    VIEW_TYPE_USER_MESSAGE_OTHER
                }
            }
            is FileMessage -> {
                NOT_YET_IMPLEMENTED
                /*  val fileMessage = message as FileMessage
                   if (message.type.toLowerCase(Locale.ROOT).startsWith("image")) { }*/
            }
            else -> {
                -1
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                return MyUserMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_my_chat_message,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                return OtherUserMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_oppo_chat_message,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_ADMIN_MESSAGE -> {
                return AdminUserMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_admin,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return OthersViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.item_simple_text,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: BaseMessage = messageList[position]
        var isContinuous: Boolean = false
        var isNewDay: Boolean = false

        // If there is at least one item preceding the current one, check the previous message.
        if (position < messageList.size - 1) {
            val prevMessage: BaseMessage = messageList.get(position + 1)

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateTimeUtils.hasSameDate(message.createdAt, prevMessage.createdAt)) {
                isNewDay = true
                isContinuous = false
            } else {
                isContinuous = isContinuous(message, prevMessage)
            }
        } else if (position == messageList.size - 1) {
            isNewDay = true
        }

        return when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                (holder as MyUserMessageHolder).bind(message as UserMessage, isContinuous, isNewDay)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                (holder as OtherUserMessageHolder).bind(message as UserMessage, isContinuous, isNewDay)
            }
            VIEW_TYPE_ADMIN_MESSAGE -> {
                (holder as AdminUserMessageHolder).bind(message as AdminMessage, isContinuous, isNewDay)
            }
            else -> {
                (holder as OthersViewHolder).bindView()
            }
        }
    }

    inner class MyUserMessageHolder(val binding: ItemMyChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: UserMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message

            channel?.let { binding.tvMessageStatus.drawMessageStatus(it, message) }
        }
    }

    inner class OtherUserMessageHolder(val binding: ItemOppoChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: UserMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message
        }
    }

    inner class AdminUserMessageHolder(val binding: ListItemGroupChatAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: AdminMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay
        }
    }

    inner class OthersViewHolder(private val binding: ItemSimpleTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            binding.data = "Other Message Type : Not implemented yet!!"
        }
    }

    /**
     * Checks if the current message was sent by the same person that sent the preceding message.
     *
     *
     * This is done so that redundant UI, such as sender nickname and profile picture,
     * does not have to displayed when not necessary.
     */
    private fun isContinuous(currentMsg: BaseMessage?, precedingMsg: BaseMessage?): Boolean {
        // null check
        if (currentMsg == null || precedingMsg == null) {
            return false
        }
        if (currentMsg is AdminMessage && precedingMsg is AdminMessage) {
            return true
        }
        var currentUser: User? = null
        var precedingUser: User? = null
        if (currentMsg is UserMessage) {
            currentUser = currentMsg.sender
        } else if (currentMsg is FileMessage) {
            currentUser = currentMsg.sender
        }
        if (precedingMsg is UserMessage) {
            precedingUser = precedingMsg.sender
        } else if (precedingMsg is FileMessage) {
            precedingUser = precedingMsg.sender
        }

        // If admin message or
        return (!(currentUser == null || precedingUser == null)
                && currentUser.userId == precedingUser.userId)
    }

    @Synchronized
    private fun isMessageListLoading(): Boolean {
        return mIsMessageListLoading
    }

    @Synchronized
    private fun setMessageListLoading(tf: Boolean) {
        mIsMessageListLoading = tf
    }

    fun markAllMessagesAsRead() {
        channel?.markAsRead { e1 -> e1?.printStackTrace() }
        // notifyDataSetChanged()
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load or refresh.
     */
    fun loadLatestMessages(limit: Int, function: () -> Unit) {

        if (isMessageListLoading() || channel == null) {
            return
        }
        setMessageListLoading(true)
        val params = MessageListParams().apply {
            nextResultSize = limit
            reverse = false
        }
        channel!!.getMessagesByTimestamp(Long.MAX_VALUE, params) { messages, e ->
            setMessageListLoading(false)
            if (e != null) {
                e.printStackTrace()
                return@getMessagesByTimestamp
            }
            if (messages?.size?:0 <= 0) {
                return@getMessagesByTimestamp
            }
            addData(messages!!)
        }
        /* channel?.getMessagesByTimestamp(
             Long.MAX_VALUE,
             true,
             limit,
             true,
             MessageTypeFilter.ALL,
             null,
             null,
             false,
             true,
             GetMessagesHandler { list, e ->
                 handler?.onResult(list, e)
                 setMessageListLoading(false)
                 if (e != null) {
                     e.printStackTrace()
                     return@GetMessagesHandler
                 }
                 if (list.size <= 0) {
                     return@GetMessagesHandler
                 }
 //                for (message in mMessageList) {
 //                    if (isTempMessage(message) || isFailedMessage(message)) {
 //                        list.add(0, message)
 //                    }
 //                }
                 addData(list)
             })*/
    }

    /**
     * Load old message list.
     * @param limit
     * @param handler
     */
    fun loadPreviousMessages(limit: Int) {
        if (isMessageListLoading() || channel == null) {
            return
        }
        var oldestMessageCreatedAt = Long.MAX_VALUE
        if (messageList.size > 0) {
            oldestMessageCreatedAt = messageList.get(messageList.size - 1).createdAt
        }
        setMessageListLoading(true)
        val params = MessageListParams().apply {
            nextResultSize = limit
            reverse = false
        }
        channel!!.getMessagesByTimestamp(oldestMessageCreatedAt, params) { messages, e ->
            if (e != null) {
                e.printStackTrace()
                return@getMessagesByTimestamp
            }
            if (messages.isNullOrEmpty()) {
                return@getMessagesByTimestamp
            }
            if (!messages.isNullOrEmpty()) {
                addMessages(messages)
                if (messages.size >= params.nextResultSize) {
                    loadPreviousMessages(limit)
                } else {
                    setMessageListLoading(true)
                }
            } else {
                setMessageListLoading(false)
            }
        }
    }

    private class ChatMessageDiffCallback : DiffUtil.ItemCallback<BaseMessage>() {
        override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val URL_PREVIEW_CUSTOM_TYPE = "url_preview"

        private val VIEW_TYPE_USER_MESSAGE_ME = 10
        private val VIEW_TYPE_USER_MESSAGE_OTHER = 11
        private val VIEW_TYPE_ADMIN_MESSAGE = 30

        private val NOT_YET_IMPLEMENTED = 101
    }
}