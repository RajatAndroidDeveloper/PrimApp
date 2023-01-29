package com.primapp.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.databinding.FragmentCreateChannelBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.extensions.showSuccess
import com.primapp.model.chat.ChatUser
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.chat.adapter.MentorMenteeChatAdapter
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CreateChannelFragment : BaseFragment<FragmentCreateChannelBinding>() {

    private var searchJob: Job? = null

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    val adapter by lazy { MentorMenteeChatAdapter { item -> onItemClick(item) } }

    override fun getLayoutRes(): Int = R.layout.fragment_create_channel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.new_message), toolbar)
        setData()
        setObserver()
        setAdapter()
        initTextListeners()
    }

    private fun setAdapter() {
        binding.rvChatUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvChatUsers.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.rvChatUsers.isVisible = true

                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error

                    else -> null
                }
                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        showError(requireContext(), it.error.localizedMessage)
                    }
                }

            } else {
                binding.swipeRefresh.isRefreshing = true
                binding.rvChatUsers.isVisible = false
            }
        }
    }

    private fun setData() {
        binding.frag = this
        searchChatUsers(null)
    }

    private fun setObserver() {
    }

    private fun createChanel(userId: String) {
        showLoading()
        GroupChannel.createChannelWithUserIds(listOf(userId), true) { groupChannel, e ->
            hideLoading()
            if (e != null) {
                Log.e(
                    ConnectionManager.TAG,
                    "Failed to create channel with this user. Reason : ${e.message} -|- Cause : ${e.cause}"
                )
                showError(requireContext(), "Failed to create channel with this user.")
            } else {
                Log.d(ConnectionManager.TAG, "Channel created success Url: ${groupChannel.url}")
                //showSuccess(requireContext(), "Channel created")
                val bundle = Bundle()
                bundle.putString("channelUrl", groupChannel.url)
                findNavController().navigate(R.id.action_createChannelFragment_to_chatFragment, bundle)
            }
        }
    }

    private fun checkUserExistAndCreateChannel(userId: String) {
        showLoading()
        val listQuery = SendBird.createApplicationUserListQuery()
        listQuery.setUserIdsFilter(arrayListOf(userId))
        listQuery.next { mutableList: MutableList<User>?, e: SendBirdException? ->
            hideLoading()
            if (e != null || mutableList == null || mutableList.size<=0) {
                showError(requireContext(),"User doesn't exist on Sendbird.")
                return@next
            }
            createChanel(userId)
        }
    }


    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(any: Any?) {
        when (any) {
            is ChatUser -> {
                checkUserExistAndCreateChannel(any.id.toString())
            }
        }
    }

    private fun initTextListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateChatUserListFromInput()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateChatUserListFromInput()
                true
            } else {
                false
            }
        }

        binding.etSearch.addTextChangedListener {
            if (it.isNullOrEmpty()) {
               updateChatUserListFromInput()
            }
        }
    }

    private fun updateChatUserListFromInput() {
        binding.etSearch.text.trim().let {
            if (it.isNotEmpty()) {
                searchChatUsers(it.toString())
            } else {
                searchChatUsers(null)
            }
        }
    }

    private fun searchChatUsers(query: String?) {
        searchJob?.cancel()
        searchJob =  lifecycleScope.launch {
            viewModel.getMentorMenteeMemberList(UserCache.getUserId(requireContext()), query)
                .observe(viewLifecycleOwner, Observer {
                    adapter.submitData(lifecycle, it)
                })
        }
    }

}