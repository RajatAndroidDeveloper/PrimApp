package com.primapp.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
import com.sendbird.android.*
import kotlinx.android.synthetic.main.toolbar_community_back.*

class ChannelListFragment : BaseFragment<FragmentChannelListBinding>() {

    val userData by lazy { UserCache.getUser(requireContext()) }

    lateinit var groupChannelListQuery: GroupChannelListQuery

    val adapter by lazy { ChannelListAdapter { item -> onItemClick(item) } }

    override fun getLayoutRes(): Int = R.layout.fragment_channel_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.message), toolbar)
        setData()
        setAdapter()
        initTextListeners()
    }

    private fun setData() {
        binding.frag = this

        ivAdd.setOnClickListener {
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

        SendBird.addChannelHandler(CHANNEL_HANDLER, object : SendBird.ChannelHandler() {
            override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {
                adapter.updateChannel(p0 as? GroupChannel)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID)
        SendBird.removeChannelHandler(CHANNEL_HANDLER)
    }

    fun refresh() {
        showLoader(true)
        groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery()
        groupChannelListQuery.limit = CHANNEL_LIST_LIMIT
        groupChannelListQuery.isIncludeEmpty = true
        if (binding.etSearch.text.trim().isNotEmpty()) {
            groupChannelListQuery.nicknameContainsFilter = binding.etSearch.text.trim().toString()
        }

        groupChannelListQuery.next { list, e ->
            showLoader(false)
            if (e != null) {
                Log.e(ConnectionManager.TAG, "Failed to fetch the channels")
                e.printStackTrace()
                return@next
            }
            Log.d(ConnectionManager.TAG, "Group channels fetched : ${list.size}")
            adapter.setChannels(list)
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
        binding.progressBar.isVisible = isVisible
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