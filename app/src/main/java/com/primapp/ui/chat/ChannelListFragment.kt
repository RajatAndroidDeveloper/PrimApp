package com.primapp.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.databinding.FragmentChannelListBinding
import com.primapp.extensions.setDivider
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.chat.adapter.ChannelListAdapter
import com.primapp.ui.dashboard.DashboardActivity
import com.sendbird.android.*
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.channel.query.GroupChannelListQuery
import com.sendbird.android.channel.query.GroupChannelListQueryOrder
import com.sendbird.android.channel.query.MyMemberStateFilter
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.GroupChannelHandler
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.params.GroupChannelListQueryParams
import kotlinx.android.synthetic.main.toolbar_community_back.*
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.ivMenu

class ChannelListFragment : BaseFragment<FragmentChannelListBinding>() {

    val userData by lazy { UserCache.getUser(requireContext()) }

    lateinit var groupChannelListQuery: GroupChannelListQuery

    val adapter by lazy { ChannelListAdapter { item -> onItemClick(item) } }

    override fun getLayoutRes(): Int = R.layout.fragment_channel_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.messages), toolbar)
        setData()
        setAdapter()
        initTextListeners()
    }

    private fun setData() {
        binding.frag = this
        ivCreateNewMessage.isVisible = true
        ivAdd.isVisible = false

        ivCreateNewMessage.setOnClickListener {
            findNavController().navigate(R.id.createChannelFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectionManager.addConnectionManagementHandler(userData!!.id.toString(), CONNECTION_HANDLER_ID,
            object : ConnectionManager.ConnectionManagementHandler {
                override fun onConnected(reconnect: Boolean) {
                    refresh()
                }
            })

        SendbirdChat.addChannelHandler(CHANNEL_HANDLER, object : GroupChannelHandler() {
            override fun onMessageReceived(channel: BaseChannel, message: BaseMessage) {
                adapter.updateChannel(channel as? GroupChannel)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID)
        SendbirdChat.removeChannelHandler(CHANNEL_HANDLER)
    }

    fun refresh() {
        showLoader(true)
        var param = if (binding.etSearch.text.trim().isNotEmpty()) {
            GroupChannelListQueryParams(
                order = GroupChannelListQueryOrder.LATEST_LAST_MESSAGE,
                myMemberStateFilter = MyMemberStateFilter.ALL,
                channelNameContainsFilter = binding.etSearch.text.trim().toString(),
                limit = CHANNEL_LIST_LIMIT
            )
        } else{
            GroupChannelListQueryParams(
                order = GroupChannelListQueryOrder.LATEST_LAST_MESSAGE,
                myMemberStateFilter = MyMemberStateFilter.ALL,
                limit = CHANNEL_LIST_LIMIT
            )
        }
        groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery(param)

        groupChannelListQuery.next { list, e ->
            showLoader(false)
            if (e != null) {
                Log.e(ConnectionManager.TAG, "Failed to fetch the channels")
                e.printStackTrace()
                return@next
            }
            Log.d(ConnectionManager.TAG, "Group channels fetched : ${list?.size}")
            adapter.setChannels(list as MutableList<GroupChannel>)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadNextChannels() {
        if (!this::groupChannelListQuery.isInitialized) {
            return
        }
        groupChannelListQuery.next { list, e ->
            if (e != null) {
                Log.e(ConnectionManager.TAG, "Failed to fetch next channels")
                e.printStackTrace()
                return@next
            }
            adapter.addChannels(list)
        }
    }

    fun setAdapter() {
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvChannelsList.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }
        binding.rvChannelsList.adapter = adapter

        // If user scrolls to bottom of the list, loads more channels.
        binding.rvChannelsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    loadNextChannels()
                }
            }
        })
    }

    private fun initTextListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                refresh()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                refresh()
                true
            } else {
                false
            }
        }

        binding.etSearch.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                refresh()
            }
        }

    }

    private fun showLoader(isVisible: Boolean) {
        binding.progressBar.isVisible = isVisible && !binding.swipeRefresh.isRefreshing
        binding.rvChannelsList.isVisible = !isVisible
    }

    private fun onItemClick(any: Any) {
        when (any) {
            is GroupChannel -> {
                val bundle = Bundle()
                bundle.putString("channelUrl", any.url)
                findNavController().navigate(R.id.chatFragment, bundle)
            }
        }
    }

    companion object {
        const val CHANNEL_LIST_LIMIT = 20
        const val CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
        const val CHANNEL_HANDLER = "ChannelListFragment_ChannelHandler"
    }
}