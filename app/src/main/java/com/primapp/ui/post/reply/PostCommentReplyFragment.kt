package com.primapp.ui.post.reply

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentPostCommentReplyBinding
import com.primapp.extensions.showError
import com.primapp.extensions.smoothScrollTo
import com.primapp.model.LikeReply
import com.primapp.model.comment.CommentData
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostCommentReplyFragment : BaseFragment<FragmentPostCommentReplyBinding>() {

    lateinit var mainCommentData: CommentData

    lateinit var postData: PostListResult

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { PostCommentReplyPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_post_comment_reply

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.replies), toolbar)
        setData()
        setAdapter()
        setObserver()
        setClicks()
    }

    private fun setData() {
        mainCommentData = PostCommentReplyFragmentArgs.fromBundle(requireArguments()).mainCommentData
        postData = PostCommentReplyFragmentArgs.fromBundle(requireArguments()).postData
        binding.mainCommentData = mainCommentData
        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getCommentsReply(postData.community.id, mainCommentData.id)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.submitData(it)
                    }
                }
            })

        viewModel.createCommentReplyLiveData.observe(viewLifecycleOwner, Observer {
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
                        mainCommentData.replyCount++
                        DialogUtils.showCloseDialog(requireActivity(), R.string.reply_posted_success) {
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
                            mainCommentData.isLike = true
                            mainCommentData.likeCount++
                            binding.mainCommentData = mainCommentData
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
                            mainCommentData.isLike = false
                            mainCommentData.likeCount--
                            binding.mainCommentData = mainCommentData
                        }
                    }
                }
            }
        })

        viewModel.likeReplyLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markReplyAsLiked(it.replyId)
                        }
                    }
                }
            }
        })

        viewModel.unlikeReplyLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.markReplyAsDisliked(it.replyId)
                        }
                    }
                }
            }
        })
    }

    fun postReply() {
        //If community is not joined don't allow to post reply
        if (!postData.community.isJoined) {
            DialogUtils.showCloseDialog(
                requireActivity(),
                R.string.non_joined_community_action_error_message,
                R.drawable.question_mark
            )
            return
        }
        //If community is inactive don't allow to post reply
        if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
            DialogUtils.showCloseDialog(
                requireActivity(),
                R.string.inactive_community_action_message,
                R.drawable.question_mark
            )
            return
        }

        val replyText = binding.etReply.text.toString().trim()
        if (replyText.isNotEmpty()) {
            viewModel.createCommentReply(
                postData.community.id,
                userData!!.id,
                postData.id,
                mainCommentData.id,
                replyText
            )
        }
    }

    private fun setAdapter() {
        binding.rvCommentsReply.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvCommentsReply.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.progressBar.isVisible = false
                binding.rvCommentsReply.isVisible = true

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
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.progressBar.isVisible = true
                    binding.rvCommentsReply.isVisible = false
                }
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    fun onItemClick(any: Any?) {
        when (any) {

            is LikeReply -> {
                //If community is not joined don't allow to post reply
                if (!postData.community.isJoined) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.non_joined_community_action_error_message,
                        R.drawable.question_mark
                    )
                    return
                }
                //If community is inactive don't allow to post reply
                if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.inactive_community_action_message,
                        R.drawable.question_mark
                    )
                    return
                }

                if (any.replyData.isLike) {
                    viewModel.unlikeReply(
                        postData.community.id,
                        userData!!.id,
                        postData.id,
                        mainCommentData.id,
                        any.replyData.id
                    )
                } else {
                    viewModel.likeReply(
                        postData.community.id,
                        userData!!.id,
                        postData.id,
                        mainCommentData.id,
                        any.replyData.id
                    )
                }
            }
        }
    }

    private fun setClicks() {
        binding.includeMainComment.tvCommentLike.setOnClickListener {
            //If community is not joined don't allow to post reply
            if (!postData.community.isJoined) {
                DialogUtils.showCloseDialog(
                    requireActivity(),
                    R.string.non_joined_community_action_error_message,
                    R.drawable.question_mark
                )
                return@setOnClickListener
            }
            //If community is inactive don't allow to post reply
            if (postData.community.status.equals(CommunityStatusTypes.INACTIVE, true)) {
                DialogUtils.showCloseDialog(
                    requireActivity(),
                    R.string.inactive_community_action_message,
                    R.drawable.question_mark
                )
                return@setOnClickListener
            }

            if (mainCommentData.isLike) {
                viewModel.unlikeComment(postData.community.id, userData!!.id, postData.id, mainCommentData.id)
            } else {
                viewModel.likeComment(postData.community.id, userData!!.id, postData.id, mainCommentData.id)
            }
        }

        binding.includeMainComment.tvCommentReply.setOnClickListener {
            binding.etReply.requestFocus()
            showKeyBoard(binding.etReply)
        }

        binding.includeMainComment.tvRepliesCount.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(binding.rvCommentsReply)
        }
    }
}