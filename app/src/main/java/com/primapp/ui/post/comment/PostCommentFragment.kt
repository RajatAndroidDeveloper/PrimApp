package com.primapp.ui.post.comment

import android.os.Bundle
import android.util.Log
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
import com.primapp.databinding.FragmentPostCommentBinding
import com.primapp.extensions.showError
import com.primapp.model.LikeComment
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class PostCommentFragment : BaseFragment<FragmentPostCommentBinding>() {

    lateinit var postData: PostListResult

    val userData by lazy { UserCache.getUser(requireContext()) }

    val adapter by lazy { CommentsListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_post_comment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.comments), toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        postData = PostCommentFragmentArgs.fromBundle(requireArguments()).postData
        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getPostCommentsListData(postData.community.id, userData!!.id, postData.id)
            .observe(viewLifecycleOwner, Observer {
                it?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.submitData(it)
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
    }

    fun postComment() {
        //If community is not joined don't allow to post comment
        if(!postData.community.isJoined){
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
        val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        lm.stackFromEnd = true
        binding.rvComments.apply {
            layoutManager = lm
        }

        binding.rvComments.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
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
                if (!binding.swipeRefresh.isRefreshing) {
                    binding.progressBar.isVisible = true
                    binding.rvComments.isVisible = false
                }
            }
        }

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    Log.d("anshul", "----data observer called : ${it.prepend.endOfPaginationReached}")
                    if (it.prepend.endOfPaginationReached && adapter.itemCount > 0) {
                        binding.rvComments.postDelayed({
                            binding.rvComments.smoothScrollToPosition(0)
                        }, 300)
                    }
                }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    fun onItemClick(any: Any?) {
        when (any) {
            is LikeComment -> {
                //If community is not joined don't allow to post comment
                if(!postData.community.isJoined){
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
}