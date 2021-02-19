package com.primapp.ui.post

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentUpdatesBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.*
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.fragment_updates.*
import kotlinx.coroutines.launch

class UpdatesFragment : BaseFragment<FragmentUpdatesBinding>() {

    val userData by lazy { UserCache.getUser(requireContext()) }

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
        binding.userData = userData

        binding.groupNoPostView.isVisible = userData!!.joinedCommunityCount == 0
        binding.groupNoCommunityView.isVisible = userData!!.joinedCommunityCount > 0
    }

    private fun setObserver() {

        viewModel.getPostsList().observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            }
        })

        viewModel.likePostLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markPostAsLiked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.unlikePostLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markPostAsDisliked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.deletePostLiveData.observe(viewLifecycleOwner, Observer {
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
                            UserCache.decrementPostCount(requireContext())
                            adapter.removePost(it.postId)
                        }
                    }
                }
            }
        })
    }

    private fun setAdapter() {
        val lm = LinearLayoutManager(requireContext())
        binding.rvCommunityPosts.apply {
            layoutManager = lm
        }

        binding.rvCommunityPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = lm.findFirstVisibleItemPosition()

                if (firstVisibleItem > 1) {
                    //  && dy < 0 Show FAB if 1st item is not visible and scrolling upside
                    binding.tvScrollUp.visibility = View.VISIBLE
                } else {
                    //Hide FAB
                    binding.tvScrollUp.visibility = View.GONE
                }
            }
        })

        binding.tvScrollUp.setOnClickListener {
            rvCommunityPosts.smoothScrollToPosition(0)
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
                if (!binding.swipeRefresh.isRefreshing && userData!!.joinedCommunityCount != 0 && adapter.itemCount < 1) {
                    binding.pbPost.isVisible = true
                }
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    fun createPost() {
        val action =
            UpdatesFragmentDirections.actionUpdatesFragmentToCreatePostFragment(null)
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
            is LikePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (item.postData.isLike) {
                    viewModel.unlikePost(item.postData.community.id, userData!!.id, item.postData.id)
                } else {
                    viewModel.likePost(item.postData.community.id, userData!!.id, item.postData.id)
                }
            }

            is EditPost -> {
                val bundle = Bundle()
                bundle.putString("type", CreatePostFragment.UPDATE_POST)
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.createPostFragment, bundle)
            }

            is HidePost, is ReportPost -> {
                showInfo(requireContext(), "Not yet implemented!")
            }

            is DeletePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                DialogUtils.showYesNoDialog(requireActivity(), R.string.delete_post_message, {
                    viewModel.deletePost(item.postData.community.id, userData!!.id, item.postData.id)
                })
            }

            is CommentPost -> {
                val bundle = Bundle()
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.postCommentFragment, bundle)
            }

            is LikePostMembers -> {
                val bundle = Bundle()
                bundle.putInt("communityId", item.postData.community.id)
                bundle.putInt("postId", item.postData.id)
                bundle.putString("type", CommunityMembersFragment.POST_LIKE_MEMBERS_LIST)
                findNavController().navigate(R.id.communityMembersFragment, bundle)
            }

            is ShowUserProfile -> {
                val bundle = Bundle()
                bundle.putInt("userId", item.postData.user.id)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }
        }
    }
}