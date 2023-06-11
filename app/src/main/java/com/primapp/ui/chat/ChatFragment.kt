package com.primapp.ui.chat

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.chat.SendBirdHelper
import com.primapp.constants.EmoticonHelper
import com.primapp.databinding.FragmentChatBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.MessageLongPressCallback
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.base.ImageViewDialogArgs
import com.primapp.ui.chat.adapter.ChatAdapter
import com.primapp.ui.chat.adapter.ChatRecyclerDataObserver
import com.primapp.utils.DialogUtils
import com.primapp.utils.DownloadUtils
import com.primapp.utils.FileUtils
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.*
import com.sendbird.android.channel.*
import com.sendbird.android.handler.*
import com.sendbird.android.message.*
import com.sendbird.android.params.FileMessageCreateParams
import com.sendbird.android.params.MessageListParams
import com.sendbird.android.params.common.MessagePayloadFilter
import com.sendbird.android.user.User
import kotlinx.android.synthetic.main.layout_pdf_view.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import java.io.File
import java.io.Serializable
import java.util.*
import javax.inject.Inject


class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private var mChannel: GroupChannel? = null
    lateinit var currentChannelUrl: String
    private var isLoadingFirstTime: Boolean = false
    private var mIsTyping: Boolean = false

    val adapter by lazy { ChatAdapter { item -> onItemClick(item) } }

    val userData by lazy { UserCache.getUser(requireContext()) }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    @Inject
    lateinit var downloadManager: DownloadManager

    override fun getLayoutRes(): Int = R.layout.fragment_chat
    private lateinit var recyclerObserver: ChatRecyclerDataObserver

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setAdapter()
        initListeners()
        setObserver()
    }

    private fun setObserver() {
        viewModel.checkMentorMenteeRelationLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            binding.llChatBox.isVisible = !it.isEmpty()
                            if (it.isEmpty()) {
                                showInfo(requireContext(), getString(R.string.no_relation_message))
                            }
                        }
                    }
                }
            }
        })
    }

    private fun checkRelation(function: () -> Unit) {
        if (mChannel == null) {
            return
        }
        viewModel.checkMentorMenteeRelation(userId = SendBirdHelper.getGroupChannelOtherUserId(mChannel!!))
    }

    private fun initListeners() {
        binding.etMessage.addTextChangedListener {
            binding.btnSend.isEnabled = it?.length ?: -1 > 0
            if (!mIsTyping) {
                setTypingStatus(true)
            }

            if (it?.length == 0) {
                setTypingStatus(false)
            }
        }
    }

    private fun setAdapter() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mLayoutManager.stackFromEnd = true
        binding.rvChat.apply {
            layoutManager = mLayoutManager
        }

        binding.rvChat.adapter = adapter
        recyclerObserver = ChatRecyclerDataObserver(binding.rvChat, adapter)
        adapter.registerAdapterDataObserver(recyclerObserver)
        binding.rvChat.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1)) {
                    if (hasPrevious && !adapter.isMessageListLoading() && adapter.messageList.isNotEmpty()) {
                        loadMessagesPreviousMessagesData(adapter.messageList.first().createdAt)
                    }
                }
            }
        })

//        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                if (mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
//                    adapter.loadPreviousMessages(
//                        CHANNEL_LIST_LIMIT
//                    )
//                }
//            }
//        })
    }

    private fun setData() {
        currentChannelUrl = ChatFragmentArgs.fromBundle(requireArguments()).channelUrl

        binding.frag = this
        binding.btnSend.isEnabled = false

        tvClearChat.setOnClickListener {
            DialogUtils.showYesNoDialog(requireActivity(), R.string.delete_chat_history, {
                resetMyChannelHistory()
            })
        }
    }

    private fun resetMyChannelHistory() {
        if (mChannel == null) {
            return
        }
        showLoading()
        mChannel?.resetMyHistory {
            hideLoading()
            if (it != null) {
                showError(requireContext(), "Failed to remove chat history")
                Log.d(ConnectionManager.TAG, "Failed to remove chat history. Cause: ${it.cause} Code: ${it.code} ")
            }
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        ConnectionManager.addConnectionManagementHandler(userData!!.id.toString(),
            CONNECTION_HANDLER_ID,
            object : ConnectionManager.ConnectionManagementHandler {
                override fun onConnected(reconnect: Boolean) {
                    refresh()
                }
            })

        SendbirdChat.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : GroupChannelHandler() {
                override fun onMessageReceived(baseChannel: BaseChannel, baseMessage: BaseMessage) {
                    if (baseChannel.url == currentChannelUrl) {
                        adapter.markAllMessagesAsRead()
                        adapter.addFirst(baseMessage)
                        adapter.notifyItemChanged(adapter.messageList.size)
                        recyclerObserver.scrollToBottom(true)
                        tvOnlineStatus.isVisible = false
                    }
                }

                override fun onMessageDeleted(baseChannel: BaseChannel, msgId: Long) {
                    super.onMessageDeleted(baseChannel, msgId)
                    if (baseChannel.url == currentChannelUrl) {
                        adapter.deleteMessage(msgId)
                    }
                }

                override fun onMessageUpdated(channel: BaseChannel, message: BaseMessage) {
                    super.onMessageUpdated(channel, message)
                    if (channel.url == currentChannelUrl) {
                        adapter.updateMessage(message)
                    }
                }

//                override fun onReadReceiptUpdated(channel: GroupChannel) {
//                    if (channel.url == currentChannelUrl) {
//                        adapter.notifyDataSetChanged()
//                        tvOnlineStatus.isVisible = false
//                    }
//                }

                override fun onTypingStatusUpdated(channel: GroupChannel) {
                    if (channel.url == currentChannelUrl) {
                        tvOnlineStatus.isVisible = false
                        val typingUsers =
                            channel.typingUsers
                        displayTyping(typingUsers)
                    }
                }

//                override fun onDeliveryReceiptUpdated(channel: GroupChannel) {
//                    if (channel.url == currentChannelUrl) {
//                        adapter.notifyDataSetChanged()
//                    }
//                }

//                override fun onReactionUpdated(channel: BaseChannel?, reactionEvent: ReactionEvent?) {
//                    super.onReactionUpdated(channel, reactionEvent)
//                    reactionEvent?.let {
//                        adapter.addReaction(reactionEvent)
//                    }
//                }
            })
    }

    private fun displayTyping(typingUsers: List<User>) {
        binding.llChatEvents.isVisible = typingUsers.isNotEmpty()
    }

    override fun onPause() {
        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID)
        SendbirdChat.removeChannelHandler(CHANNEL_HANDLER_ID)
        super.onPause()
    }

    fun refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(currentChannelUrl) getChannelLabel@{ groupChannel, e ->
                if (e != null) {
                    Log.e(ConnectionManager.TAG, "Failed to get the channel")
                    e.printStackTrace()
                    return@getChannelLabel
                }
                adapter.channel = groupChannel
                mChannel = groupChannel
                updateActionBarTitle()
                isLoadingFirstTime = true

                loadMessagesPreviousMessagesData(Long.MAX_VALUE)

//                adapter.loadLatestMessages(CHANNEL_LIST_LIMIT, ) {
//                    mChannel?.markAsRead { e1 -> e1?.printStackTrace() }
//                     recyclerObserver.scrollToBottom(true)
//                }

//                checkRelation {
//                    mChannel?.markAsRead { e1 -> e1?.printStackTrace() }
//                     recyclerObserver.scrollToBottom(true)
//                }

                setReactionHandler()
            }
        } else {
            mChannel!!.refresh(CompletionHandler { e ->
                if (e != null) {
                    Log.e(ConnectionManager.TAG, "Failed to refresh the channel")
                    e.printStackTrace()
                    return@CompletionHandler
                }
                updateActionBarTitle()
            })
        }
    }

    private fun updateActionBarTitle() {
        tvTitle?.text = SendBirdHelper.getGroupChannelTitle(mChannel!!)
        if (SendBirdHelper.getMembersOnlineStatus(mChannel!!)) {
            tvOnlineStatus?.text = getString(R.string.online)
        } else {
            tvOnlineStatus?.text = SendBirdHelper.getMembersLastSeen(mChannel!!)
        }
    }

    fun sendUserMessage() {
        val userInput: String = binding.etMessage.text.toString()
        if (userInput.isNotEmpty()) {
            sendMessage(userInput)
            binding.etMessage.setText("")
        }
    }

    private fun sendMessage(userInput: String) {
        if (mChannel == null) {
            return
        }

        val tempUserMessage: UserMessage =
            mChannel!!.sendUserMessage(userInput, UserMessageHandler { userMessage, e ->
                if (e != null) {
                    // Error!
                    Log.e(ConnectionManager.TAG, e.toString())
                    userMessage?.let { adapter.updateMessage(it) }
                    return@UserMessageHandler
                }
                Log.d(ConnectionManager.TAG, "Message Sent : $userInput")
                // Update a sent message to RecyclerView
                userMessage?.let { adapter.updateMessage(it) }
            })

        // Display a user message to RecyclerView
        adapter.addFirst(tempUserMessage)
        //recyclerObserver.scrollToBottom(true)
    }

    private fun setTypingStatus(typing: Boolean) {
        if (mChannel == null) {
            return
        }
        if (typing) {
            mIsTyping = true
            mChannel!!.startTyping()
        } else {
            mIsTyping = false
            mChannel!!.endTyping()
        }
    }

    private fun onItemClick(any: Any) {
        when (any) {
            is MessageLongPressCallback -> {
                showMessageOptionsDialog(
                    any.message,
                    any.message.sender?.userId == SendbirdChat.currentUser?.userId,
                    any.message is FileMessage
                )
            }

            is FileMessage -> {
                val type: String = any.type.toLowerCase(Locale.ROOT)
                if (type.startsWith("image")) {
                    val bundle = Bundle()
                    bundle.putString("url", any.url)
                    findNavController().navigate(R.id.imageViewDialog, bundle)
                } else if (type.startsWith("video")) {
                    val bundle = Bundle()
                    bundle.putString("url", any.url)
                    findNavController().navigate(R.id.videoViewDialog, bundle)
                } else {
                    if (!any.url.isNullOrEmpty()) {
                        DownloadUtils.download(requireContext(), downloadManager, any.url)
                    }
                }
            }
        }
    }

    //To listen to events fired by BottomSheet
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.chatFragment)

        // Create our observer and add it to the NavBackStackEntry's lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                when {
                    navBackStackEntry.savedStateHandle.contains("emoticonToSend") -> {
                        val type = navBackStackEntry.savedStateHandle.get<String>("emoticonToSend")
                        val key = navBackStackEntry.savedStateHandle.get<String>("emojiKey")
                        val oldKey = navBackStackEntry.savedStateHandle.get<String>("oldEmojiKey")
                        // Do something with the result
                        val serializedMessage = navBackStackEntry.savedStateHandle.get<Serializable>("message");
                        serializedMessage?.let {
                            val message = BaseMessage.buildFromSerializedData(it as? ByteArray)
                            when (type) {
                                EmoticonHelper.ADD_REACTION -> {
                                    message?.let { it1 -> addReaction(it1, key!!) }
                                }
                                EmoticonHelper.REMOVE_REACTION -> {
                                    message?.let { it1 -> removeReaction(it1, key!!) }
                                }
                                EmoticonHelper.UPDATE_REACTION -> {
                                    message?.let { it1 -> updateReaction(it1, key!!, oldKey!!) }
                                }
                                else -> {
                                    Log.e("No message", "No data")
                                }
                            }
                        }
                    }
                    navBackStackEntry.savedStateHandle.contains("copyMessage") -> {
                        val result = navBackStackEntry.savedStateHandle.get<Serializable>("copyMessage");
                        result?.let {
                            val message = BaseMessage.buildFromSerializedData(it as? ByteArray)
                            copyTextToClipboard(message!!.message)
                        }
                    }
                    navBackStackEntry.savedStateHandle.contains("deleteMessage") -> {
                        val result = navBackStackEntry.savedStateHandle.get<Serializable>("deleteMessage");
                        result?.let {
                            val message = BaseMessage.buildFromSerializedData(it as? ByteArray)
                            deleteMessage(message!!)
                        }
                    }
                }

            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun showMessageOptionsDialog(
        message: BaseMessage,
        isMyMessage: Boolean,
        isFileMessage: Boolean
    ) {
        val bundle = Bundle()
        bundle.putSerializable("message", message.serialize())
        bundle.putBoolean("isMyMessage", isMyMessage)
        bundle.putBoolean("isFileMessage", isFileMessage)
        findNavController().navigate(R.id.bottomSheetChatOptions, bundle)
    }

    private fun deleteMessage(message: BaseMessage) {
        if (mChannel == null) {
            return
        }

        mChannel!!.deleteMessage(message, CompletionHandler { e ->
            if (e != null) {
                // Error!
                showError(requireContext(), "Failed to delete the message")
                return@CompletionHandler
            }
        })
    }

    //Reactions on Message

    private fun addReaction(message: BaseMessage, reaction: String) {
        mChannel?.addReaction(message, reaction) handler@{ event, e ->
            if (e != null) {
                e.printStackTrace()
                showError(requireContext(), "Failed to add reaction to the message")
            }
            if (event == null) return@handler
            message.applyReactionEvent(event)
            adapter.notifyDataSetChanged()
        }
    }

    private fun removeReaction(message: BaseMessage, reaction: String) {
        mChannel?.deleteReaction(message, reaction) handler@{ event, e ->
            if (e != null) {
                e.printStackTrace()
                showError(requireContext(), "Failed to remove reaction from the message")
            }
            if (event == null) return@handler
            Log.d("anshul", "Reaction Removed")
        }
    }

    private fun updateReaction(message: BaseMessage, reactionToAdd: String, reactionToRemove: String) {
        removeReaction(message, reactionToRemove)
        addReaction(message, reactionToAdd)
        Log.d("anshul", "Reaction Updated")
    }

    //For attachment to send files

    fun requestMediaAskPermission() {
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            requestMedia()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestMedia() {
        startActivityForResult(FileUtils.requestAnyFile(requireContext()), FileUtils.PICK_ANY_FILE_REQUEST_CODE)
        // Set this as false to maintain connection
        // even when an external Activity is started.
        SendbirdChat.autoBackgroundDetection = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isGranted = isAllPermissionsGranted(grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (isGranted) {
                    requestMedia()
                } else {
                    val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                    if (!showRationale) {
                        /*
                        *   Permissions are denied permanently, redirect to permissions page
                        * */
                        showError(requireContext(), getString(R.string.error_camera_permission))
                        redirectUserToAppSettings()
                    } else {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.error_camera_permission,
                            R.drawable.question_mark
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Set this as true to restore background connection management.
        SendbirdChat.autoBackgroundDetection = true
        if (requestCode == FileUtils.PICK_ANY_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
//            if (data == null) {
//                Log.d("Sendbird_File_Upload", "data is null!")
//                return
//            }

            if (data?.data == null) {
                val file = FileUtils.getFile(requireContext(), FileUtils.IMAGE)
                sendFileWithThumbnail(file)
            } else {
                val file = FileUtils.getFileFromUri(requireContext(), data.data!!)
                Log.e("AsasasasasA",file?.extension!!)
                if (file?.extension == "pdf") {
                    showPdfDialog(data.data!!.toString(), file)
                } else {
                    sendFileWithThumbnail(file)
                }
            }
        }
    }

    private fun sendFileWithThumbnail(fileVal: File?) {
        if (fileVal == null || mChannel == null) return

        // Specify two dimensions of thumbnails to generate
        val thumbnailSizes: MutableList<ThumbnailSize> = ArrayList()
        thumbnailSizes.add(ThumbnailSize(240, 240))
        thumbnailSizes.add(ThumbnailSize(320, 320))

        val info: Hashtable<String, Any?>? = FileUtils.getFileInfo(requireContext(), fileVal)

        if (info == null || info.isEmpty) {
            showError(requireContext(), "Invalid file selected, can't access information.")
            return
        }

        var fileSize = (fileVal.length() / 1024) / 1024
        Log.d(FileUtils.FILE_PICK_TAG, "Sending File Message, Size : $fileSize")

        if (fileSize > 18) {
            showError(requireContext(), getString(R.string.video_file_size_error_message))
            return
        }

        val name: String?
        name = if (info.containsKey("name")) {
            info["name"] as String?
        } else {
            "Sendbird File"
        }
        val mime = info["mime"] as String?
        val size = info["size"] as Int

        if (mime?.toLowerCase(Locale.ROOT)?.startsWith("image") == true) {
            FileUtils.compressImage(fileVal.absolutePath)
        }

        val params = FileMessageCreateParams().apply {
            file = fileVal
            fileName = fileVal.name
            fileSize = size.toLong()
            this.thumbnailSizes = thumbnailSizes
            mimeType = mime
        }
        val tempFileMessage: FileMessage? = mChannel!!.sendFileMessage(
            params
        ) { fileMessage, e ->

            if (e != null) {
                // Error!
                Log.e(ConnectionManager.TAG, e.toString())
                fileMessage?.let { adapter.updateMessage(it) }
                return@sendFileMessage
            }
            Log.d(ConnectionManager.TAG, "File Message Sent")
            // Update a sent message to RecyclerView
            fileMessage?.let { adapter.updateMessage(it) }
        }

        tempFileMessage?.let { adapter.addFirst(it) }
        recyclerObserver.scrollToBottom(true)
    }

    /*    //To show date as sticky
        private var sectionItemDecoration: RecyclerDateSectionItemDecoration? = null

        private fun addSectionDecorator() {
            sectionItemDecoration = RecyclerDateSectionItemDecoration(
                resources.getDimensionPixelSize(R.dimen.space_extraLarge),
                true,
                getSectionCallback()
            )

            if (binding.rvChat.itemDecorationCount != 0)
                binding.rvChat.removeItemDecoration(sectionItemDecoration!!)
            binding.rvChat.addItemDecoration(sectionItemDecoration!!)
        }

        private fun getSectionCallback(): RecyclerDateSectionItemDecoration.SectionCallback {
            return object : RecyclerDateSectionItemDecoration.SectionCallback {
                override fun isSection(position: Int): Boolean {
                    try {
                        return position == 0 ||
                                DateTimeUtils.formatDate(adapter.messageList.get(position).createdAt) !=
                                DateTimeUtils.formatDate(adapter.messageList.get(position - 1).createdAt)
                    } catch (e: Exception) {
                        return false
                    }
                }

                override fun getSectionHeader(position: Int): CharSequence {
                    try {
                        return DateTimeUtils.formatDate(adapter.messageList.get(position).createdAt)
                            .toString()
                    } catch (e: Exception) {
                        return ""
                    }
                }
            }


        }
    */

    private fun setReactionHandler() {
        SendbirdChat.addChannelHandler(ReactionHandlerKey, object : GroupChannelHandler() {
            override fun onMessageReceived(channel: BaseChannel, message: BaseMessage) {
                Log.e("asasasas121212", "asasasas")
            }

            override fun onReactionUpdated(channel: BaseChannel, reactionEvent: ReactionEvent) {
                val messageId = reactionEvent.messageId
                val message = adapter.messageList.firstOrNull { it.messageId == messageId } ?: return
                message.applyReactionEvent(reactionEvent)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private var hasPrevious: Boolean = true
    private fun loadMessagesPreviousMessagesData(
        timeStamp: Long,
    ) {
        val channel = mChannel ?: return
        adapter.setMessageListLoading(true)
        val params = MessageListParams().apply {
            previousResultSize = 20
            nextResultSize = 0
            reverse = false
            messageTypeFilter = MessageTypeFilter.ALL
            messagePayloadFilter = MessagePayloadFilter().apply {
                includeReactions = true
            }
        }
        channel.getMessagesByTimestamp(timeStamp, params) { messages, e ->
            if (e != null) {
                e.printStackTrace()
                return@getMessagesByTimestamp
            }
            if (messages != null) {
                if (messages.isNotEmpty()) {
                    hasPrevious = messages.size >= params.previousResultSize
                    adapter.addPreviousMessages(messages)
                    adapter.notifyDataSetChanged()
                    adapter.markAllMessagesAsRead()
                } else {
                    hasPrevious = false
                }
            }
            adapter.setMessageListLoading(false)
        }
    }

    private fun loadToLatestMessages(timeStamp: Long) {
        val channel = mChannel ?: return
        adapter.setMessageListLoading(true)
        val params = MessageListParams().apply {
            nextResultSize = 30
            reverse = false
            messageTypeFilter = MessageTypeFilter.ALL
            messagePayloadFilter = MessagePayloadFilter().apply {
                includeReactions = true
            }
        }
        channel.getMessagesByTimestamp(timeStamp, params) { messages, e ->
            if (e != null) {
                e.printStackTrace()
                return@getMessagesByTimestamp
            }
            if (!messages.isNullOrEmpty()) {
                adapter.addNextMessages(messages)
                if (messages.size >= params.nextResultSize) {
                    loadToLatestMessages(messages.last().createdAt)
                } else {
                    adapter.setMessageListLoading(false)
                }
            } else {
                adapter.setMessageListLoading(false)
            }
        }
    }

    companion object {
        const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
        const val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"
        private const val CHAT_READ_HANDLER = "chat_read_handler"
        private const val ReactionHandlerKey = "ReactionHandler"
        const val CHANNEL_LIST_LIMIT = 30
    }

    private var pdfDialog: Dialog? = null
    private fun showPdfDialog(url: String, file: File) {
        pdfDialog = Dialog(requireActivity(), R.style.DialogAnimation)
        pdfDialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        pdfDialog!!.setContentView(R.layout.layout_pdf_view)
        pdfDialog!!.setCancelable(true)

        pdfDialog!!.imageView.fromUri(Uri.parse(url))
            .enableAnnotationRendering(true)
            .spacing(10)
            .load()

        pdfDialog?.ivClose?.setOnClickListener {
            pdfDialog?.dismiss()
        }

        pdfDialog?.ivSend?.setOnClickListener {
            pdfDialog?.dismiss()
            sendFileWithThumbnail(file)
        }

        if (!pdfDialog!!.isShowing) {
            pdfDialog!!.show()
        }
    }
}