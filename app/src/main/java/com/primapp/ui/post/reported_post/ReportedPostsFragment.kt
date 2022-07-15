package com.primapp.ui.post.reported_post

import android.app.DownloadManager
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
import com.primapp.model.*
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.post.reported_post.adapter.SimplePostListPagedAdapter
import com.primapp.utils.DialogUtils
import com.primapp.utils.DownloadUtils
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportedPostsFragment : BaseFragment<FragmentReportedPostsBinding>() {

    private var communityId: Int? = null

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { SimplePostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    @Inject
    lateinit var downloadManager: DownloadManager

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

        adapter.setCurrentLoggedInUser(userId = userData!!.id)

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

        viewModel.removeCulpritMembersLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.postId?.let{
                            adapter.removePost(it)
                        }
                    }
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
                binding.tvNothingToShow.isVisible = false

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

                if (loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    binding.rvCommunityPosts.isVisible = false
                    binding.tvNothingToShow.isVisible = true
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
            is DownloadFile -> {
                DownloadUtils.download(requireContext(), downloadManager, item.url)
            }

            is RemoveReportedUser -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_reported_user_consent, {
                    viewModel.removeCulpritMembers(communityId!!,item.postId, item.userId)
                })
            }

            is ReportedByMembers -> {
                val bundle = Bundle()
                bundle.putInt("communityId", communityId!!)
                bundle.putInt("postId", item.postId)
                findNavController().navigate(R.id.reportByMembersFragment, bundle)
            }
        }
    }

    companion object {
        const val REPORTED_POST = "reportedPosts"
    }
}