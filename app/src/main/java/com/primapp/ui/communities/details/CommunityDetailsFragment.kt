package com.primapp.ui.communities.details

import android.app.DownloadManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityFilterTypes
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentCommunityDetailsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.extensions.showNormalToast
import com.primapp.model.*
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityMembersImageAdapter
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.notification.MentorRequestRejectionFragment
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.ui.post.create.CreatePostFragment
import com.primapp.utils.DialogUtils
import com.primapp.utils.DownloadUtils
import com.primapp.utils.OverlapItemDecorantion
import com.primapp.utils.visible
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.item_list_community.*
import kotlinx.android.synthetic.main.toolbar_menu_more.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityDetailsFragment : BaseFragment<FragmentCommunityDetailsBinding>() {

    lateinit var communityData: CommunityData

    val userAdapter by lazy { CommunityMembersImageAdapter() }

    val postsAdapter by lazy { PostListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    val userData by lazy { UserCache.getUser(requireContext()) }

    @Inject
    lateinit var downloadManager: DownloadManager

    override fun getLayoutRes(): Int = R.layout.fragment_community_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setObserver()
        setAdapter()
        setClicks()
        showPopUpMenu()
    }

    private fun showPopUpMenu() {
        if (communityData.isCreatedByMe == false) {
            return
        }

        ivMore.setOnClickListener {
            //creating a popup menu
            val popup = PopupMenu(requireContext(), ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.post_details_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { p0 ->
                when (p0?.itemId) {
                    R.id.reportedPost -> {
                        val bundle = Bundle()
                        bundle.putInt("communityId", communityData.id)
                        findNavController().navigate(R.id.reportedPostsFragment, bundle)
                    }
                }

                false
            }
            //displaying the popup
            popup.show()
        }

        ivMore.isVisible = true
    }

    private fun setData() {
        communityData = CommunityDetailsFragmentArgs.fromBundle(requireArguments()).communityData
        binding.type = CommunityFilterTypes.COMMUNITY_DETAILS
        binding.data = communityData
        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getCommunityDetailsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        showAdditionalData(true)
                        response.data?.content?.apply {
                            communityData.isJoined = isJoined
                            communityData.isCreatedByMe = isCreatedByMe
                            communityData.communityName = communityName
                            communityData.communityDescription = communityDescription
                            communityData.imageUrl = imageUrl
                            communityData.communityCreator = communityCreator
                            communityData.status = status
                            communityData.udate = udate
                            communityData.totalActiveMember = totalActiveMember
                            communityData.cdate = cdate
                            binding.data = communityData
                            userAdapter.addData(communityJoiner)
                        }
                    }
                    Status.LOADING -> {
                        showAdditionalData(false)
                    }
                    Status.ERROR -> {
                        showError(requireContext(), response.message!!)
                        findNavController().popBackStack()
                    }
                }
            }
        })

        viewModel.getCommunityDetails(communityData.id)

        viewModel.joinCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                hideLoading()
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.content?.apply {
                            communityData.isJoined = isJoined
                            communityData.totalActiveMember = totalActiveMember
                            if (isJoined == true) {
                                UserCache.incrementJoinedCommunityCount(requireContext())
                            } else {
                                UserCache.decrementJoinedCommunityCount(requireContext())
                            }
                        }

                        binding.data = communityData
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

        viewModel.getCommunityPostsListData(communityData.id).observe(viewLifecycleOwner, Observer {
            it?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    postsAdapter.submitData(it)
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
                            postsAdapter.markPostAsLiked(it.postId)
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
                            postsAdapter.markPostAsDisliked(it.postId)
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
                            postsAdapter.removePost(it.postId)
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
                            postsAdapter.addPostToBookmark(it.postId)
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
                            postsAdapter.removePostAsBookmarked(it.postId)
                        }
                    }
                }
            }
        })

        viewModel.hidePostLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            postsAdapter.removePost(it.postId)
                        }
                    }
                }
            }
        })
    }

    private fun showAdditionalData(visibility: Boolean) {
        // Show progressbar if additional data is not visible
        binding.pbAdditionalData.visible(!visibility)
        binding.llAdditionalData.visible(visibility)
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.rvMembersImages.apply {
            this.layoutManager = layoutManager
            addItemDecoration(OverlapItemDecorantion())

        }
        binding.rvMembersImages.adapter = userAdapter


        // Posts Adapter

        binding.rvCommunityPosts.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvCommunityPosts.adapter = postsAdapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { postsAdapter.retry() },
            footer = CommunityPagedLoadStateAdapter { postsAdapter.retry() }
        )

        postsAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.rvCommunityPosts.isVisible = true

                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error

                    else -> null
                }
                error?.let {
                    if (postsAdapter.snapshot().isEmpty()) {
                        showError(requireContext(), it.error.localizedMessage)
                    }
                }

            } else {
                binding.rvCommunityPosts.isVisible = false
            }
        }
    }

    private fun setClicks() {
        btnJoin.setOnClickListener {
            if (communityData.isCreatedByMe == true) {
                val bundle = Bundle()
                bundle.putSerializable("communityData", communityData)
                findNavController().navigate(R.id.editCommunityFragment, bundle)
            } else {
                if (communityData.isJoined == true) {
                    DialogUtils.showYesNoDialog(
                        requireActivity(),
                        R.string.leave_Community_message,
                        yesClickCallback = {
                            val bundle = Bundle()
                            bundle.putSerializable("communityData", communityData)
                            bundle.putString("type", MentorRequestRejectionFragment.LEAVE_COMMUNITY)
                            findNavController().navigate(R.id.mentorRequestRejectionFragment, bundle)
                        })
                } else {
                    viewModel.joinCommunity(communityData.id, userData!!.id)
                }
            }
        }

        cardProfilePic.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("url", communityData.imageUrl.toString())
            findNavController().navigate(R.id.imageViewDialog, bundle)
        }
    }

    fun showAllMembers() {
        val bundle = Bundle()
        bundle.putInt("communityId", communityData.id)
        bundle.putString("type", CommunityMembersFragment.COMMUNITY_MEMBERS_LIST)
        findNavController().navigate(R.id.communityMembersFragment, bundle)
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
            is LikePost -> {
                if (item.postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }
                if (communityData.isJoined == false) {
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

                if (communityData.isJoined == false) {
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
                DialogUtils.showYesNoDialog(requireActivity(), R.string.hide_post_confirmation, {
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
                bundle.putInt("userId", item.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }
            is SharePost -> {
                sharePostAsImage(
                    item.view,
                    "${item.postData.user.firstName} ${item.postData.user.lastName}",
                    item.postData.community.communityName
                )
            }
            is LoadWebUrl -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.sensitive_content))
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.commonWebView, bundle)
            }
        }
    }
}