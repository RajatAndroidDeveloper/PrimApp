package com.primapp.ui.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.*
import com.primapp.model.MessageLongPressCallback
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.isOnlyEmoji
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.GetMessagesHandler
import java.util.*

class ChatAdapter constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mIsMessageListLoading = false
    var channel: GroupChannel? = null

    private val messageList: ArrayList<BaseMessage> = arrayListOf()

    fun addData(listData: List<BaseMessage>) {
        messageList.clear()
        messageList.addAll(listData)
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        messageList.add(0, message)
        notifyItemInserted(0)
    }

    fun addMessages(messages: List<BaseMessage>) {
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

    fun deleteMessage(msgId: Long) {
        val messageInList = messageList.find { it.messageId == msgId }
        val index = messageList.indexOf(messageInList)
        if (index != -1)
            messageList.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemViewType(position: Int): Int {
        val message: BaseMessage = messageList[position]
        return when (message) {
            is UserMessage -> {
                if (message.sender.userId == SendBird.getCurrentUser().userId) {
                    VIEW_TYPE_USER_MESSAGE_ME
                } else {
                    VIEW_TYPE_USER_MESSAGE_OTHER
                }
            }
            is AdminMessage -> {
                VIEW_TYPE_ADMIN_MESSAGE
            }
            is FileMessage -> {

                return if (message.type.toLowerCase(Locale.ROOT).startsWith("image") || message.type.toLowerCase(Locale.ROOT).startsWith("video")) {
                    if (message.getSender().userId == SendBird.getCurrentUser().userId) {
                        VIEW_TYPE_FILE_MESSAGE_IMAGE_ME
                    } else {
                        VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER
                    }
                } else {
                    NOT_YET_IMPLEMENTED
                }
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
                        R.layout.list_item_group_chat_user_me,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                return OtherUserMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_user_other,
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
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> {
                return MyUserImageMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_file_image_me,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> {
                return OtherUserImageMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_file_image_other,
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
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> {
                (holder as MyUserImageMessageHolder).bind(message as FileMessage, isContinuous, isNewDay)
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> {
                (holder as OtherUserImageMessageHolder).bind(message as FileMessage, isContinuous, isNewDay)
            }
            else -> {
                (holder as OthersViewHolder).bindView()
            }
        }
    }

    inner class MyUserMessageHolder(val binding: ListItemGroupChatUserMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: UserMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            if (isOnlyEmoji(message.message)) {
                Log.d("anshul_matches", message.message)
                binding.textGroupChatMessage.textSize = 40f
            } else {
                binding.textGroupChatMessage.textSize = 14f
            }

            channel?.let { binding.messageStatusGroupChat.drawMessageStatus(it, message) }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }
        }
    }

    inner class OtherUserMessageHolder(val binding: ListItemGroupChatUserOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: UserMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {

            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            if (isOnlyEmoji(message.message)) {
                Log.d("anshul_matches", message.message)
                binding.textGroupChatMessage.textSize = 40f
            } else {
                binding.textGroupChatMessage.textSize = 14f
            }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }
        }
    }

    inner class MyUserImageMessageHolder(val binding: ListItemGroupChatFileImageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: FileMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {

            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            binding.ivPlay.isVisible = message.type.toLowerCase(Locale.ROOT).startsWith("video")

            channel?.let { binding.messageStatusGroupChat.drawMessageStatus(it, message) }

            binding.root.setOnClickListener {
                onItemClick(message)
            }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }

        }
    }

    inner class OtherUserImageMessageHolder(val binding: ListItemGroupChatFileImageOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: FileMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            binding.ivPlay.isVisible = message.type.toLowerCase(Locale.ROOT).startsWith("video")

            binding.root.setOnClickListener {
                onItemClick(message)
            }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }
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

    fun isFailedMessage(message: BaseMessage): Boolean {
        return message.sendingStatus == BaseMessage.SendingStatus.FAILED
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
        channel?.markAsRead()
        // notifyDataSetChanged()
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load or refresh.
     */
    fun loadLatestMessages(limit: Int, handler: GetMessagesHandler?) {

        if (isMessageListLoading() || channel == null) {
            return
        }
        setMessageListLoading(true)
        channel?.getPreviousMessagesByTimestamp(
            Long.MAX_VALUE,
            true,
            limit,
            true,
            BaseChannel.MessageTypeFilter.ALL,
            null,
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
            })
    }

    /**
     * Load old message list.
     * @param limit
     * @param handler
     */
    fun loadPreviousMessages(limit: Int, handler: GetMessagesHandler?) {
        if (isMessageListLoading() || channel == null) {
            return
        }
        var oldestMessageCreatedAt = Long.MAX_VALUE
        if (messageList.size > 0) {
            oldestMessageCreatedAt = messageList.get(messageList.size - 1).getCreatedAt()
        }
        setMessageListLoading(true)
        channel?.getPreviousMessagesByTimestamp(
            oldestMessageCreatedAt,
            false,
            limit,
            true,
            BaseChannel.MessageTypeFilter.ALL,
            null,
            GetMessagesHandler { list, e ->
                handler?.onResult(list, e)
                setMessageListLoading(false)
                if (e != null) {
                    e.printStackTrace()
                    return@GetMessagesHandler
                }
                /* for (message in list) {
                     mMessageList.add(message)
                 }*/
                if (list.size <= 0) {
                    return@GetMessagesHandler
                }
                addMessages(list)
            })
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

        private const val VIEW_TYPE_USER_MESSAGE_ME = 10
        private const val VIEW_TYPE_USER_MESSAGE_OTHER = 11
        private const val VIEW_TYPE_ADMIN_MESSAGE = 30
        private const val VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22
        private const val VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23

        private const val NOT_YET_IMPLEMENTED = 101
    }
}