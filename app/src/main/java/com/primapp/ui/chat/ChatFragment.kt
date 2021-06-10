package com.primapp.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.chat.SendBirdHelper
import com.primapp.databinding.FragmentChatBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.chat.adapter.ChatAdapter
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.GetMessagesHandler
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import com.sendbird.android.GroupChannel.GroupChannelGetHandler
import com.sendbird.android.GroupChannel.GroupChannelRefreshHandler
import com.sendbird.android.SendBird.ChannelHandler
import kotlinx.android.synthetic.main.toolbar_chat.*

class ChatFragment : BaseFragment<FragmentChatBinding>() {


    private var mChannel: GroupChannel? = null

    lateinit var currentChannelUrl: String
    private var isLoadingFirstTime: Boolean = false
    private var mIsTyping: Boolean = false

    val adapter by lazy { ChatAdapter { item -> onItemClick(item) } }

    val userData by lazy { UserCache.getUser(requireContext()) }

    override fun getLayoutRes(): Int = R.layout.fragment_chat

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setAdapter()
        initListeners()
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
//                    if (baseChannel.url == mChannelUrl) {
//                        adapter.delete(msgId)
//                    }
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
                })

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
        tvTitle.text = SendBirdHelper.getGroupChannelTitle(mChannel!!)
        if (SendBirdHelper.getMembersOnlineStatus(mChannel!!)) {
            tvOnlineStatus.text = getString(R.string.online)
        } else {
            tvOnlineStatus.text = SendBirdHelper.getMembersLastSeen(mChannel!!)
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
        TODO("Not yet implemented")
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