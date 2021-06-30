package com.primapp.ui.chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.chat.SendBirdHelper
import com.primapp.databinding.FragmentChatBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.MessageLongPressCallback
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.chat.adapter.ChatAdapter
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.*
import com.sendbird.android.FileMessage.ThumbnailSize
import com.sendbird.android.GroupChannel.GroupChannelGetHandler
import com.sendbird.android.GroupChannel.GroupChannelRefreshHandler
import com.sendbird.android.SendBird.ChannelHandler
import kotlinx.android.synthetic.main.toolbar_chat.*
import java.io.File
import java.util.*


class ChatFragment : BaseFragment<FragmentChatBinding>() {


    private var mChannel: GroupChannel? = null

    lateinit var currentChannelUrl: String
    private var isLoadingFirstTime: Boolean = false
    private var mIsTyping: Boolean = false

    val adapter by lazy { ChatAdapter { item -> onItemClick(item) } }

    val userData by lazy { UserCache.getUser(requireContext()) }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_chat

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

    private fun checkRelation() {
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
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        mLayoutManager.stackFromEnd = true
        binding.rvChat.apply {
            layoutManager = mLayoutManager
        }

        binding.rvChat.adapter = adapter

        //addSectionDecorator()

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    adapter.loadPreviousMessages(
                        CHANNEL_LIST_LIMIT,
                        null
                    )
                }
                /* if (!recyclerView.canScrollVertically(-1)) {
                     adapter.loadPreviousMessages(
                         CHANNEL_LIST_LIMIT,
                         null
                     )
                 }*/
            }
        })

//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                Log.i("anshul_item", "start : $positionStart item count : $itemCount Total size: ${adapter.itemCount}")
//                // TO scroll chat on opening screen and adding new item only
//                if (itemCount > 0 && isLoadingFirstTime || positionStart == (adapter.itemCount - 1)) {
//                    //rvChats?.scrollToPosition(positionStart + itemCount - 1)
//                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
//                    isLoadingFirstTime = false
//                }
//
//            }
//
//        })
//
//        //To position chat when soft keyboard is open
//        binding.rvChat.addOnLayoutChangeListener { _, _, top, _: Int, bottom, _, _, _, oldBottom ->
//            if (bottom < oldBottom) {
//                binding.rvChat.postDelayed({
//                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
//                }, 100)
//            }
//        }
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

        SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : ChannelHandler() {
                override fun onMessageReceived(baseChannel: BaseChannel, baseMessage: BaseMessage) {
                    if (baseChannel.url == currentChannelUrl) {
                        adapter.markAllMessagesAsRead()
                        // Add new message to view
                        adapter.addFirst(baseMessage)
                        binding.rvChat.scrollToPosition(0)
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

                override fun onReadReceiptUpdated(channel: GroupChannel) {
                    if (channel.url == currentChannelUrl) {
                        adapter.notifyDataSetChanged()
                        tvOnlineStatus.isVisible = false
                    }
                }

                override fun onTypingStatusUpdated(channel: GroupChannel) {
                    if (channel.url == currentChannelUrl) {
                        tvOnlineStatus.isVisible = false
                        val typingUsers =
                            channel.typingUsers
                        displayTyping(typingUsers)
                    }
                }

                override fun onDeliveryReceiptUpdated(channel: GroupChannel) {
                    if (channel.url == currentChannelUrl) {
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun displayTyping(typingUsers: List<User>) {
        binding.llChatEvents.isVisible = typingUsers.isNotEmpty()
    }

    override fun onPause() {
        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID)
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
        super.onPause()
    }

    private fun refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(currentChannelUrl, GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    Log.e(ConnectionManager.TAG, "Failed to get the channel")
                    e.printStackTrace()
                    return@GroupChannelGetHandler
                }
                adapter.channel = groupChannel
                mChannel = groupChannel
                updateActionBarTitle()
                isLoadingFirstTime = true
                adapter.loadLatestMessages(CHANNEL_LIST_LIMIT, GetMessagesHandler { list, e ->
                    adapter.markAllMessagesAsRead()
                    binding.rvChat.scrollToPosition(0)
                })

                checkRelation()
            })
        } else {
            mChannel!!.refresh(GroupChannelRefreshHandler { e ->
                if (e != null) {
                    Log.e(ConnectionManager.TAG, "Failed to refresh the channel")
                    e.printStackTrace()
                    return@GroupChannelRefreshHandler
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
            mChannel!!.sendUserMessage(userInput, SendUserMessageHandler { userMessage, e ->
                if (e != null) {
                    // Error!
                    Log.e(ConnectionManager.TAG, e.toString())
                    adapter.updateMessage(userMessage)
                    return@SendUserMessageHandler
                }
                Log.d(ConnectionManager.TAG, "Message Sent : $userInput")
                // Update a sent message to RecyclerView
                adapter.updateMessage(userMessage)
            })

        // Display a user message to RecyclerView
        adapter.addFirst(tempUserMessage)
        binding.rvChat.scrollToPosition(0)
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
                    any.message.sender.userId == SendBird.getCurrentUser().userId,
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
                    showInfo(requireContext(), "File type not supported.")
                }
            }
        }
    }

    private fun showMessageOptionsDialog(
        message: BaseMessage,
        isMyMessage: Boolean,
        isFileMessage: Boolean
    ) {
        if (isFileMessage && !isMyMessage) return

        val options = if (isMyMessage) {
            if (isFileMessage)
                arrayOf("Delete message")
            else
                arrayOf("Copy message", "Delete message")
        } else {
            arrayOf("Copy message")
        }
        val builder =
            AlertDialog.Builder(requireActivity())
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                if (isFileMessage)
                    deleteMessage(message)
                else
                    copyTextToClipboard(message.message)
            } else if (which == 1) {
                deleteMessage(message)
            }
        }
        builder.create().show()
    }

    private fun deleteMessage(message: BaseMessage) {
        if (mChannel == null) {
            return
        }

        mChannel!!.deleteMessage(message, DeleteMessageHandler { e ->
            if (e != null) {
                // Error!
                showError(requireContext(), "Failed to delete the message")
                return@DeleteMessageHandler
            }
        })
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
        SendBird.setAutoBackgroundDetection(false)
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
        SendBird.setAutoBackgroundDetection(true)
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
                sendFileWithThumbnail(file)
            }

        }
    }

    private fun sendFileWithThumbnail(file: File?) {
        if (file == null || mChannel == null) return

        // Specify two dimensions of thumbnails to generate
        val thumbnailSizes: MutableList<ThumbnailSize> = ArrayList()
        thumbnailSizes.add(ThumbnailSize(240, 240))
        thumbnailSizes.add(ThumbnailSize(320, 320))

        val info: Hashtable<String, Any?>? = FileUtils.getFileInfo(requireContext(), file)

        if (info == null || info.isEmpty) {
            showError(requireContext(), "Invalid file selected, can't access information.")
            return
        }

        val fileSize = (file.length() / 1024) / 1024
        Log.d(FileUtils.FILE_PICK_TAG,"Sending File Message, Size : $fileSize")
        
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
            FileUtils.compressImage(file.absolutePath)
        }

        val tempFileMessage: FileMessage = mChannel!!.sendFileMessage(
            file,
            name,
            mime,
            size,
            "",
            null,
            thumbnailSizes
        ) { fileMessage, e ->

            if (e != null) {
                // Error!
                Log.e(ConnectionManager.TAG, e.toString())
                adapter.updateMessage(fileMessage)
                return@sendFileMessage
            }
            Log.d(ConnectionManager.TAG, "File Message Sent")
            // Update a sent message to RecyclerView
            adapter.updateMessage(fileMessage)
        }

        adapter.addFirst(tempFileMessage)
        binding.rvChat.scrollToPosition(0)
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
    companion object {
        const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
        const val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"
        const val CHANNEL_LIST_LIMIT = 30
    }
}