package com.primapp.ui.communities.members

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentCommunityMembersBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.RequestMentor
import com.primapp.model.ShowImage
import com.primapp.model.ShowUserProfile
import com.primapp.model.mentor.RequestMentorDataModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CommunityMembersFragment : BaseFragment<FragmentCommunityMembersBinding>() {

    private lateinit var viewType: String

    private var communityId: Int? = null
    private var postId: Int? = null

    private var searchJob: Job? = null

    val adapter by lazy { CommunityMembersListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_community_members

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.members), toolbar)
        setData()
        setAdapter()
        setObserver()
        initTextListeners()
    }

    private fun setData() {
        binding.frag = this
        communityId = CommunityMembersFragmentArgs.fromBundle(requireArguments()).communityId
        viewType = CommunityMembersFragmentArgs.fromBundle(requireArguments()).type
        postId = CommunityMembersFragmentArgs.fromBundle(requireArguments()).postId

        if (viewType == POST_LIKE_MEMBERS_LIST) {
            binding.etSearch.isVisible = false
            tvTitle.text = getString(R.string.likes)
        }

        searchMembers(null)
    }

    private fun setObserver() {
        viewModel.requestMentorLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.content?.mentor?.let {
                            adapter.markRequestAsSent(it.id)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                }
            }
        })
    }

    private fun searchMembers(query: String?) {
        searchJob?.cancel()
        searchJob = if (viewType == COMMUNITY_MEMBERS_LIST) {
            lifecycleScope.launch {
                viewModel.getCommunityMembers(communityId!!, query).observe(viewLifecycleOwner, Observer {
                    adapter.submitData(lifecycle, it)
                })
            }
        } else {
            lifecycleScope.launch {
                viewModel.getPostLikeMembersList(communityId!!, postId!!, query).observe(viewLifecycleOwner, Observer {
                    adapter.submitData(lifecycle, it)
                })
            }
        }
    }

    private fun setAdapter() {
        binding.rvCommunityMembers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvCommunityMembers.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.rvCommunityMembers.isVisible = true

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
                binding.rvCommunityMembers.isVisible = false
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(any: Any?) {
        when (any) {
            is RequestMentor -> {
                viewModel.requestMentor(
                    any.membersData.community!!.id, UserCache.getUserId(requireContext()),
                    RequestMentorDataModel(any.membersData.user.id, null)
                )
            }

            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", any.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }

            is ShowUserProfile -> {
                val bundle = Bundle()
                bundle.putInt("userId", any.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }
        }
    }

    private fun initTextListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateCommunityMembersListFromInput()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCommunityMembersListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateCommunityMembersListFromInput() {
        binding.etSearch.text.trim().let {
            if (it.isNotEmpty()) {
                searchMembers(it.toString())
            } else {
                searchMembers(null)
            }
        }
    }

    companion object {
        const val COMMUNITY_MEMBERS_LIST = "communityMembersList"
        const val POST_LIKE_MEMBERS_LIST = "postLikeMembersList"
    }

}