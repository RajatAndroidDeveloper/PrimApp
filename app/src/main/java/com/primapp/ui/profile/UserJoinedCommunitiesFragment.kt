package com.primapp.ui.profile

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentUserJoinedCommunitiesBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.JoinedCommunityFilterType
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ActionCommunityDetails
import com.primapp.ui.communities.adapter.CommunityPagedListAdapter
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import com.primapp.viewmodels.PostsViewModel
import kotlinx.coroutines.launch

class UserJoinedCommunitiesFragment(private val userId: Int) : BaseFragment<FragmentUserJoinedCommunitiesBinding>() {

    val adapter by lazy { CommunityPagedListAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_user_joined_communities

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getAllJoinedCommunityList(JoinedCommunityFilterType.ALL, userId)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    lifecycleScope.launch {
                        adapter.submitData(it)
                    }
                }
            })

        viewModel.joinCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                hideLoading()
                when (response.status) {
                    Status.SUCCESS -> {
                        adapter.markCommunityAsJoined(response.data?.content?.id)
                        UserCache.incrementJoinedCommunityCount(requireContext())
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), response.message!!)
                    }
                }
            }
        })
    }


    private fun setAdapter() {
        binding.rvJoinedCommunity.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvJoinedCommunity.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.progressBar.isVisible = false
                binding.rvJoinedCommunity.isVisible = true

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
                /*
                if (loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    binding.groupPostView.isVisible = false
                    binding.groupNoPostView.isVisible = true
                }*/

            } else {
                //binding.swipeRefresh.isRefreshing = true
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.progressBar.isVisible = true
                    binding.rvJoinedCommunity.isVisible = false
                }
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(any: Any?) {
        when (any) {
            is ActionCommunityDetails -> {
                viewModel.joinCommunity(any.communityData!!.id, UserCache.getUser(requireContext())!!.id)
            }

            is CommunityData -> {
                val bundle = Bundle()
                bundle.putInt("communityId", any.id)
                bundle.putSerializable("communityData", any)
                findNavController().navigate(R.id.communityDetailsFragment, bundle)
            }
        }
    }
}