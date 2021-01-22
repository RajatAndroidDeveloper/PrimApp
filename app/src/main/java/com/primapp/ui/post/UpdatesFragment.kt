package com.primapp.ui.post

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentUpdatesBinding
import com.primapp.extensions.showError
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.base.VideoViewDialog
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.post.UpdatesFragmentDirections
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.adapter.ShowImage
import com.primapp.ui.post.adapter.ShowVideo
import com.primapp.viewmodels.PostsViewModel
import kotlinx.coroutines.launch

class UpdatesFragment : BaseFragment<FragmentUpdatesBinding>() {

    var joinedCommunityResponse: JoinedCommunityListModel? = null

    val adapter by lazy { PostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_updates

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

        viewModel.joinedCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.pbPost.isVisible = false
                when (it.status) {
                    Status.SUCCESS -> {
                        joinedCommunityResponse = it.data
                        it.data?.let {
                            binding.groupNoPostView.isVisible = it.content.isEmpty()
                            binding.groupNoCommunityView.isVisible = it.content.isNotEmpty()
                            binding.groupPostView.isVisible = it.content.isNotEmpty()
                        }
                    }

                    Status.LOADING -> {
                        binding.pbPost.isVisible = true
                    }

                    Status.ERROR -> {
                    }
                }
            }
        })

        viewModel.getJoinedCommunityList()

        viewModel.getParentCategoriesListData().observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            }
        })
    }

    private fun setAdapter() {
        binding.rvCommunityPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvCommunityPosts.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.pbPost.isVisible = false

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
                if (!binding.swipeRefresh.isRefreshing)
                    binding.pbPost.isVisible = true
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    fun createPost() {
        val action =
            UpdatesFragmentDirections.actionUpdatesFragmentToCreatePostFragment(
                joinedCommunityResponse
            )
        findNavController().navigate(action)
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }
            is ShowVideo -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.videoViewDialog, bundle)
            }
        }
    }
}