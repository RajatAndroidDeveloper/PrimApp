package com.primapp.ui.post.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityStatusTypes
import com.primapp.constants.PostFileType
import com.primapp.databinding.FragmentPostDetailsBinding
import com.primapp.extensions.setAllOnClickListener
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.LikeComment
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.model.comment.CommentData
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.members.CommunityMembersFragment
import com.primapp.ui.post.comment.CommentsListPagedAdapter
import com.primapp.ui.post.comment.PostCommentFragmentArgs
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.fragment_other_user_profile.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.android.synthetic.main.toolbar_inner_back.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostDetailsFragment : BaseFragment<FragmentPostDetailsBinding>() {

    lateinit var postData: PostListResult

    var postId: Int = -1
    var communityId: Int = -1

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { CommentsListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_post_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setAdapter()
        setObserver()
        setPostClicks()
    }

    private fun setData() {
        postId = PostDetailsFragmentArgs.fromBundle(requireArguments()).postId
        communityId = PostDetailsFragmentArgs.fromBundle(requireArguments()).communityId
        binding.frag = this
        binding.includePostCard.ivBookmark.isVisible = false
        binding.includePostCard.ivMore.isVisible = false
    }

    private fun setObserver() {
        viewModel.postDetails(communityId, postId)

        viewModel.postDetailsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading(true)
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        showLoading(false)
                        it.data?.let {
                            postData = it.content
                            binding.data = postData
                            getPostCommentsList()
                        }
                    }
                }
            }
        })

        viewModel.createCommentLiveData.observe(viewLifecycleOwner, Observer {
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
                        DialogUtils.showCloseDialog(requireActivity(), R.string.comment_posted_success) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.likeCommentLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markCommentAsLiked(it.commentId)
                        }
                    }
                }
            }
        })

        viewModel.unlikeCommentLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markCommentAsDisliked(it.commentId)
                        }
                    }
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
                            postData.isLike = true
                            postData.postLikes++
                            binding.data = postData
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
                            postData.isLike = false
                            postData.postLikes--
                            binding.data = postData
                        }
                    }
                }
            }
        })
    }

    fun getPostCommentsList() {
        viewModel.getPostCommentsListData(postData.community.id, userData!!.id, postData.id)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.submitData(it)
                    }
                }
            })
    }

    fun postComment() {
        //If community is not joined don't allow to post comment
        if (!postData.community.isJoined) {
            DialogUtils.showCloseDialog(
                requireActivity(),
                R.string.non_joined_community_action_error_message,
                R.drawable.question_mark
            )
            return
        }
        //If community is inactive don't allow to post comment
        if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
            DialogUtils.showCloseDialog(
                requireActivity(),
                R.string.inactive_community_action_message,
                R.drawable.question_mark
            )
            return
        }

        val commentText = binding.etComment.text.toString().trim()
        if (commentText.isNotEmpty()) {
            viewModel.createComment(postData.community.id, userData!!.id, postData.id, commentText)
        }
    }

    private fun setAdapter() {
        val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvComments.apply {
            layoutManager = lm
        }

        binding.rvComments.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.progressBar.isVisible = false
                binding.rvComments.isVisible = true

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
                binding.progressBar.isVisible = true
                binding.rvComments.isVisible = false

            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    fun showLoading(visible: Boolean) {
        // if loader is shown then hide other views
        binding.includePostCard.llItemPost.isVisible = !visible
        binding.clCommentView.isVisible = !visible
        binding.progressBar.isVisible = visible
    }

    fun onItemClick(any: Any?) {
        when (any) {
            is LikeComment -> {
                //If community is not joined don't allow to post comment
                if (!postData.community.isJoined) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.non_joined_community_action_error_message,
                        R.drawable.question_mark
                    )
                    return
                }
                //If community is inactive don't allow to post comment
                if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (any.commentData.isLike) {
                    viewModel.unlikeComment(postData.community.id, userData!!.id, postData.id, any.commentData.id)
                } else {
                    viewModel.likeComment(postData.community.id, userData!!.id, postData.id, any.commentData.id)
                }
            }

            is CommentData -> {
                val bundle = Bundle()
                bundle.putSerializable("mainCommentData", any)
                bundle.putSerializable("postData", postData)
                findNavController().navigate(R.id.postCommentReplyFragment, bundle)
            }
        }
    }

    fun setPostClicks() {
        binding.includePostCard.ivLike.setOnClickListener {
            if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                DialogUtils.showCloseDialog(
                    requireActivity(),
                    R.string.inactive_community_action_message,
                    R.drawable.question_mark
                )

            } else if (!postData.community.isJoined) {
                DialogUtils.showCloseDialog(
                    requireActivity(),
                    R.string.non_joined_community_action_error_message,
                    R.drawable.question_mark
                )
            } else {
                if (postData.isLike) {
                    viewModel.unlikePost(postData.community.id, userData!!.id, postData.id)
                } else {
                    viewModel.likePost(postData.community.id, userData!!.id, postData.id)
                }
            }
        }

        binding.includePostCard.groupProfileInfo.setAllOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putInt("userId", postData.user.id)
            findNavController().navigate(R.id.otherUserProfileFragment, bundle)
        })

        binding.includePostCard.ivComment.setOnClickListener {
            binding.appBar.setExpanded(false)
            binding.etComment.requestFocus()
            showKeyBoard(binding.etComment)
        }

        binding.includePostCard.tvCommentCount.setOnClickListener {
            binding.appBar.setExpanded(false)
        }

        binding.includePostCard.tvLikesCount.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("communityId", postData.community.id)
            bundle.putInt("postId", postData.id)
            bundle.putString("type", CommunityMembersFragment.POST_LIKE_MEMBERS_LIST)
            findNavController().navigate(R.id.communityMembersFragment, bundle)
        }

        binding.includePostCard.ivShare.setOnClickListener {
            showInfo(requireContext(), getString(R.string.not_yet_implemented))
        }

        binding.includePostCard.cardPostAttachment.setOnClickListener {
            when (postData.fileType) {
                PostFileType.VIDEO -> {
                    val bundle = Bundle()
                    bundle.putString("url", postData.imageUrl.toString())
                    findNavController().navigate(R.id.videoViewDialog, bundle)
                }
                PostFileType.IMAGE, PostFileType.GIF -> {
                    val bundle = Bundle()
                    bundle.putString("url", postData.imageUrl.toString())
                    findNavController().navigate(R.id.imageViewDialog, bundle)
                }
            }
        }

    }
}