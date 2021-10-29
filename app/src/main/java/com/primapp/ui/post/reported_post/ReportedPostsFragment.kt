package com.primapp.ui.post.reported_post

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
import com.primapp.databinding.FragmentReportedPostsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.reported_post.adapter.SimplePostListPagedAdapter
import com.primapp.ui.profile.UserPostsFragmentArgs
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.launch

class ReportedPostsFragment : BaseFragment<FragmentReportedPostsBinding>() {

    private var communityId: Int? = null

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { SimplePostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_reported_posts

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.reported_posts), toolbar)

        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        arguments?.let {
            communityId = ReportedPostsFragmentArgs.fromBundle(requireArguments()).communityId
        }

        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getUserPostsListData(REPORTED_POST, communityId!!.toInt()).observe(viewLifecycleOwner, Observer {
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
                binding.rvCommunityPosts.isVisible = true

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
                //binding.swipeRefresh.isRefreshing = true
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.pbPost.isVisible = true
                    binding.rvCommunityPosts.isVisible = false
                }
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
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

            REMOVE_MEMBER_ACTION -> {
                showInfo(requireContext(),"remove member coming soon")
            }

            SHOW_MEMBERS_REPORTED_ACTION -> {
                showInfo(requireContext(),"Show member coming soon")
            }
        }
    }

    companion object {
        const val REPORTED_POST = "reportedPosts"
        const val REMOVE_MEMBER_ACTION = "removeMember"
        const val SHOW_MEMBERS_REPORTED_ACTION = "showWhoReportedPost"
    }
}