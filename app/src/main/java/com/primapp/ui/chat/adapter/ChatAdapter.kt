package com.primapp.ui.chat.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.*
import com.primapp.model.MessageLongPressCallback
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.equalDate
import com.primapp.utils.equalTime
import com.primapp.utils.isOnlyEmoji
import com.sendbird.android.*
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.MessageTypeFilter
import com.sendbird.android.message.*
import com.sendbird.android.params.MessageListParams
import com.sendbird.android.params.common.MessagePayloadFilter
import com.sendbird.android.user.User
import kotlinx.android.synthetic.main.bottom_sheet_chat_options.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter constructor(val onItemClick: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mIsMessageListLoading = false
    var channel: GroupChannel? = null

    val messageList: ArrayList<BaseMessage> = arrayListOf()

    fun addData(listData: List<BaseMessage>) {
        messageList.clear()
        messageList.addAll(listData)
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        messageList.add(message)
        notifyItemRangeInserted(messageList.size, messageList.size)
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
        val messageIdIndexMap =
            messageList.mapIndexed { index, message ->
                message.messageId to index
            }.toMap()
        val resultMessageList = mutableListOf<BaseMessage>().apply { addAll(messageList) }
        val index = messageIdIndexMap[msgId]
        if (index != null) {
            resultMessageList.remove(messageList[index])
        }
        messageList.clear()
        messageList.addAll(resultMessageList)
        notifyDataSetChanged()
    }

    fun addReaction(reactionEvent: ReactionEvent) {
        val messageId = reactionEvent.messageId
        val message = messageList.firstOrNull { it.messageId == messageId } ?: return
        message.applyReactionEvent(reactionEvent)
        val index = messageList.indexOf(message)
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
            is AdminMessage -> {
                VIEW_TYPE_ADMIN_MESSAGE
            }
            is FileMessage -> {

                return if (message.type.toLowerCase(Locale.ROOT)
                        .startsWith("image") || message.type.toLowerCase(Locale.ROOT).startsWith("video")
                ) {
                    if (message.sender?.userId == SendbirdChat.currentUser?.userId) {
                        VIEW_TYPE_FILE_MESSAGE_IMAGE_ME
                    } else {
                        VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER
                    }
                } else {
                    if (message.sender?.userId == SendbirdChat.currentUser?.userId) {
                        VIEW_TYPE_FILE_MESSAGE_ME
                    } else {
                        VIEW_TYPE_FILE_MESSAGE_OTHER
                    }
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
            VIEW_TYPE_FILE_MESSAGE_ME -> {
                return MyUserFileMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_file_me,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FILE_MESSAGE_OTHER -> {
                return OtherUserFileMessageHolder(
                    DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.list_item_group_chat_file_other,
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: BaseMessage = messageList[position]

        var isContinuous: Boolean = false
        var isNewDay: Boolean = false

        // If there is at least one item preceding the current one, check the previous message.
        if (position < messageList.size - 1) {
            val prevMessage: BaseMessage = messageList[position + 1]

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

        if (position > 0) {
            isNewDay = !messageList[position].createdAt.equalDate(messageList[position - 1].createdAt)
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
            VIEW_TYPE_FILE_MESSAGE_ME -> {
                (holder as MyUserFileMessageHolder).bind(message as FileMessage, isContinuous, isNewDay)
            }
            VIEW_TYPE_FILE_MESSAGE_OTHER -> {
                (holder as OtherUserFileMessageHolder).bind(message as FileMessage, isContinuous, isNewDay)
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

            //Show Reactions on Message
            if (message.reactions.isNotEmpty()) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
            }

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

            //Show Reactions on Message
            if (message.reactions.isNotEmpty()) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
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

            //Show Reactions on Message
            if (message.reactions.size > 0) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
            }

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

            //Show Reactions on Message
            if (message.reactions.size > 0) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
            }

            binding.root.setOnClickListener {
                onItemClick(message)
            }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }
        }
    }

    inner class MyUserFileMessageHolder(val binding: ListItemGroupChatFileMeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: FileMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {

            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            channel?.let { binding.messageStatusGroupChat.drawMessageStatus(it, message) }

            //Show Reactions on Message
            if (message.reactions.size > 0) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
            }

            binding.cardGroupChatMessage.setOnClickListener {
                onItemClick(message)
            }

            binding.cardGroupChatMessage.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }

            binding.root.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
            }

        }
    }

    inner class OtherUserFileMessageHolder(val binding: ListItemGroupChatFileOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: FileMessage,
            isContinuous: Boolean,
            isNewDay: Boolean
        ) {
            binding.message = message
            binding.isContinuous = isContinuous
            binding.isNewDay = isNewDay

            //Show Reactions on Message
            if (message.reactions.size > 0) {
                val adapter = ChatMessageReactionAdapter()
                val reactionsList = ArrayList(message.reactions)
                binding.rvReactions.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                }
                binding.rvReactions.adapter = adapter
                adapter.addData(reactionsList)
            }

            binding.cardGroupChatMessage.setOnClickListener {
                onItemClick(message)
            }

            binding.cardGroupChatMessage.setOnLongClickListener {
                onItemClick(MessageLongPressCallback(message, absoluteAdapterPosition))
                true
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
        return message.sendingStatus == SendingStatus.FAILED
    }

    @Synchronized
    fun isMessageListLoading(): Boolean {
        return mIsMessageListLoading
    }

    @Synchronized
    fun setMessageListLoading(tf: Boolean) {
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
            nextResultSize = 30
            reverse = false
            messageTypeFilter = MessageTypeFilter.ALL
            messagePayloadFilter = MessagePayloadFilter().apply {
                includeReactions = true
            }
        }
//       1684947163
        channel!!.getMessagesByTimestamp(0, params) { messages, e ->
            setMessageListLoading(false)
            if (e != null) {
                e.printStackTrace()
                return@getMessagesByTimestamp
            }
            if (messages?.size ?: 0 <= 0) {
                return@getMessagesByTimestamp
            }
            Log.e("asasasasasas", messages?.last()?.message + "sdsds" + messages?.first()?.message)
            addData(messages!!)
        }
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
            messageList.reverse()
            oldestMessageCreatedAt = messageList[messageList.size - 1].createdAt
        }
        setMessageListLoading(true)
        val params = MessageListParams().apply {
            nextResultSize = limit
            reverse = false
            messagePayloadFilter = MessagePayloadFilter().apply {
                includeReactions = true
            }
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

/*
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
                */
/* for (message in list) {
                     mMessageList.add(message)
                 }*//*

                if (list.size <= 0) {
                    return@GetMessagesHandler
                }
                addMessages(list)
            })
    }
*/

    private class ChatMessageDiffCallback : DiffUtil.ItemCallback<BaseMessage>() {
        override fun areItemsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: BaseMessage, newItem: BaseMessage): Boolean {
            return oldItem == newItem
        }
    }

    fun addPreviousMessages(messages: List<BaseMessage>?) {
        if (!messages.isNullOrEmpty()) {
            messageList.addAll(0, messages)
        }
    }

    fun addNextMessages(messages: List<BaseMessage>?) {
        if (!messages.isNullOrEmpty()) {
            messages.forEach {
                ListUtils.findAddMessageIndex(messageList, it).apply {
                    if (this > -1) {
                        messageList.add(this, it)
                    }
                }
            }
        }
    }

    companion object {
        const val URL_PREVIEW_CUSTOM_TYPE = "url_preview"

        private const val VIEW_TYPE_USER_MESSAGE_ME = 10
        private const val VIEW_TYPE_USER_MESSAGE_OTHER = 11
        private const val VIEW_TYPE_ADMIN_MESSAGE = 30
        private const val VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22
        private const val VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23
        private const val VIEW_TYPE_FILE_MESSAGE_ME = 24
        private const val VIEW_TYPE_FILE_MESSAGE_OTHER = 25

        private const val NOT_YET_IMPLEMENTED = 101
    }
}