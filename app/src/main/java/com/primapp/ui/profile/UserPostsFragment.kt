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
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentUserPostsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.*
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.ui.profile.other.OtherUserProfileFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.launch

class UserPostsFragment() : BaseFragment<FragmentUserPostsBinding>() {

    private var userId: Int? = null

    private var type: String = USER_POST

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { PostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_user_posts

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        arguments?.let {
            type = UserPostsFragmentArgs.fromBundle(requireArguments()).type ?: USER_POST
            userId = UserPostsFragmentArgs.fromBundle(requireArguments()).userId
        }

        if (type == BOOKMARK_POST) {
            setToolbar(getString(R.string.bookmarks), toolbar)
            //userId = UserCache.getUserId(requireContext())
        } else {
            clToolbar.isVisible = false
        }
    }

    private fun setObserver() {
        viewModel.getUserPostsListData(type, userId!!).observe(viewLifecycleOwner, Observer {
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
                            (parentFragment as? ProfileFragment)?.refreshTabs()
                            (parentFragment as? OtherUserProfileFragment)?.refreshTabs()
                            adapter.removePost(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.bookmarkPostLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.addPostToBookmark(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.removeBookmarkLiveData.observe(viewLifecycleOwner, Observer {
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
                            if (type == BOOKMARK_POST) {
                                adapter.removePost(it.postId)
                            } else {
                                adapter.removePostAsBookmarked(it.postId)
                            }
                        }
                    }
                }
            }
        })

        viewModel.hidePostLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when(it.status){
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.removePost(it.postId)
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
            is LikePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (!item.postData.community.isJoined) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.non_joined_community_action_error_message,
                        R.drawable.question_mark
                    )
                } else {
                    if (item.postData.isLike) {
                        viewModel.unlikePost(item.postData.community.id, userData!!.id, item.postData.id)
                    } else {
                        viewModel.likePost(item.postData.community.id, userData!!.id, item.postData.id)
                    }
                }
            }
            is BookmarkPost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (!item.postData.community.isJoined) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.non_joined_community_action_error_message,
                        R.drawable.question_mark
                    )
                } else {
                    if (item.postData.isBookmark) {
                        viewModel.removeBookmark(item.postData.community.id, userData!!.id, item.postData.id)
                    } else {
                        viewModel.bookmarkPost(item.postData.community.id, userData!!.id, item.postData.id)
                    }
                }
            }
            is EditPost -> {
                val bundle = Bundle()
                bundle.putString("type", CreatePostFragment.UPDATE_POST)
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.createPostFragment, bundle)
            }
            is ReportPost -> {
                val bundle = Bundle()
                bundle.putSerializable("postData", item.postData)
                findNavController().navigate(R.id.popUpReportPost, bundle)
            }
            is HidePost -> {
                DialogUtils.showYesNoDialog(requireActivity(),R.string.hide_post_confirmation,{
                    viewModel.hidePost(item.postData.id)
                })
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

                if (!item.postData.community.isJoined) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.non_joined_community_action_error_message,
                        R.drawable.question_mark
                    )
                } else {
                    DialogUtils.showYesNoDialog(requireActivity(), R.string.delete_post_message, {
                        viewModel.deletePost(item.postData.community.id, userData!!.id, item.postData.id)
                    })
                }
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
                bundle.putInt("userId", item.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }
        }
    }

    companion object {
        const val USER_POST = "userPosts"
        const val BOOKMARK_POST = "boookmarkPosts"
    }
}