package com.primapp.ui.communities.members

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.MentorMenteeUserType
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.constants.MentorshipStatusTypes
import com.primapp.databinding.FragmentCommunityMembersBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.RequestMentor
import com.primapp.model.ShowImage
import com.primapp.model.ShowUserProfile
import com.primapp.model.mentor.RequestMentorDataModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.dashboard.ProfileFragment
import com.primapp.ui.notification.MentorRequestRejectionFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CommunityMembersFragment : BaseFragment<FragmentCommunityMembersBinding>() {

    private lateinit var viewType: String

    private var communityId: Int? = null
    private var postId: Int? = null
    private var userId: Int? = null

    private var searchJob: Job? = null

    val adapter by lazy { CommunityMembersListPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_community_members

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.members), toolbar)
        setData()
        setAdapter()
        setObserver()
        initTextListeners()
    }

    private fun setData() {
        binding.frag = this
        viewType = CommunityMembersFragmentArgs.fromBundle(requireArguments()).type
        //For community Member list
        communityId = CommunityMembersFragmentArgs.fromBundle(requireArguments()).communityId
        //For post like member list
        postId = CommunityMembersFragmentArgs.fromBundle(requireArguments()).postId
        //For MentorMentee list
        userId = CommunityMembersFragmentArgs.fromBundle(requireArguments()).userId

        //Set views (default is for community members list
        when (viewType) {
            POST_LIKE_MEMBERS_LIST -> {
                binding.etSearch.isVisible = false
                tvTitle.text = getString(R.string.likes)
            }
            MENTOR_MEMBERS_LIST, MENTEE_MEMBERS_LIST -> {
                clToolbar.isVisible = false
                binding.etSearch.isVisible = false
            }
        }
        //To show community name in mentor/mentee list view type
        adapter.setViewType(type = viewType, isOtherProfile = userId != UserCache.getUserId(requireContext()))

        searchMembers(null)
    }

    private fun setObserver() {
        viewModel.requestMentorLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.content?.mentor?.let {
                            adapter.markRequestAsSent(it.id)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                }
            }
        })
    }

    private fun searchMembers(query: String?) {
        searchJob?.cancel()
        searchJob = when (viewType) {

            POST_LIKE_MEMBERS_LIST -> {
                lifecycleScope.launch {
                    viewModel.getPostLikeMembersList(communityId!!, postId!!, query)
                        .observe(viewLifecycleOwner, Observer {
                            adapter.submitData(lifecycle, it)
                        })
                }
            }

            MENTOR_MEMBERS_LIST -> {
                lifecycleScope.launch {
                    viewModel.getMentorMenteeMemberList(
                        userId!!,
                        MentorMenteeUserType.MENTOR,
                        MentorshipStatusTypes.ACCEPTED
                    )
                        .observe(viewLifecycleOwner, Observer {
                            adapter.submitData(lifecycle, it)
                        })
                }
            }

            MENTEE_MEMBERS_LIST -> {
                lifecycleScope.launch {
                    viewModel.getMentorMenteeMemberList(
                        userId!!,
                        MentorMenteeUserType.MENTEE,
                        MentorshipStatusTypes.ACCEPTED
                    )
                        .observe(viewLifecycleOwner, Observer {
                            adapter.submitData(lifecycle, it)
                        })
                }
            }

            else -> {
                //Default is Community Members List
                lifecycleScope.launch {
                    viewModel.getCommunityMembers(communityId!!, query).observe(viewLifecycleOwner, Observer {
                        adapter.submitData(lifecycle, it)
                    })
                }
            }
        }
    }

    private fun setAdapter() {
        binding.rvCommunityMembers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvCommunityMembers.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.rvCommunityMembers.isVisible = true

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
                binding.swipeRefresh.isRefreshing = true
                binding.rvCommunityMembers.isVisible = false
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(any: Any?) {
        when (any) {
            is RequestMentor -> {
                if (viewType == MENTEE_MEMBERS_LIST) {
                    val bundle = Bundle()
                    bundle.putInt("requestId", any.membersData.id)
                    bundle.putString("type", MentorRequestRejectionFragment.MENTORSHIP_END)
                    findNavController().navigate(R.id.mentorRequestRejectionFragment, bundle)
                } else {
                    viewModel.requestMentor(
                        any.membersData.community!!.id, UserCache.getUserId(requireContext()),
                        RequestMentorDataModel(any.membersData.user.id, null)
                    )
                }
            }

            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", any.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }

            is ShowUserProfile -> {
                val bundle = Bundle()
                bundle.putInt("userId", any.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }
        }
    }

    private fun initTextListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateCommunityMembersListFromInput()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCommunityMembersListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateCommunityMembersListFromInput() {
        binding.etSearch.text.trim().let {
            if (it.isNotEmpty()) {
                searchMembers(it.toString())
            } else {
                searchMembers(null)
            }
        }
    }

    companion object {
        const val COMMUNITY_MEMBERS_LIST = "communityMembersList"
        const val POST_LIKE_MEMBERS_LIST = "postLikeMembersList"
        const val MENTOR_MEMBERS_LIST = "mentorMembersList"
        const val MENTEE_MEMBERS_LIST = "menteeMemberList"
    }

}