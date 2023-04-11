package com.primapp.ui.notification

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.databinding.FragmentNotificationsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.*
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.ui.notification.adapter.NotificationsPagedAdapter
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private var notificationJob: Job? = null

    private var requestId: Int? = null
    private var notificationFilterType: String? = null

    val adapter by lazy { NotificationsPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<NotificationViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_notifications

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("Notifications", toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_NOTIFICATION)
        ivEndIcon.setImageResource(R.drawable.filter)
        ivEndIcon.setPadding(15)
        binding.frag = this

        ivEndIcon.setOnClickListener {
            DialogUtils.showNotificationFilter(requireActivity(), notificationFilterType) { filterType ->
                notificationFilterType = filterType
                getNotification(notificationFilterType)
            }
        }

        //get notification for first time
        getNotification(notificationFilterType)
    }

    private fun getNotification(filter: String?) {
        // Make sure we cancel the previous job before creating a new one
        notificationJob?.cancel()
        notificationJob = lifecycleScope.launch {
            viewModel.getUserNotification(notificationFilterType).observe(viewLifecycleOwner, Observer {
                adapter.submitData(lifecycle, it)
            })
        }
    }

    private fun setObserver() {
        viewModel.acceptRejectMentorshipLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        adapter.updateRequestAsAccepted(requestId)
                        UserCache.incrementMenteeCount(requireContext())
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

        viewModel.readAllNotificationLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        UserCache.resetNotification(requireContext())
                        (activity as? DashboardActivity)?.refreshNotificationBadge()
                    }
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                }
            }
        })

        //Reset notification Counter
        viewModel.markNotificationAsRead()
    }

    private fun setAdapter() {
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvNotifications.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false
                binding.pbNotification.isVisible = false
                binding.rvNotifications.isVisible = true

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
                    binding.pbNotification.isVisible = true
                    binding.rvNotifications.isVisible = false
                }
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(any: Any?) {
        when (any) {
            is AcceptMetorRequest -> {
                requestId = any.id
                viewModel.acceptRejectMentorship(any.id, MentorshipRequestActionType.ACCEPT, null)
            }

            is RejectMetorRequest -> {
                val bundle = Bundle()
                bundle.putInt("requestId", any.id)
                bundle.putString("type", MentorRequestRejectionFragment.MENTORSHIP_REQUEST_REJECT)
                findNavController().navigate(R.id.mentorRequestRejectionFragment, bundle)
            }

            is ShowUserProfile -> {
                val bundle = Bundle()
                bundle.putInt("userId", any.userId)
                findNavController().navigate(R.id.otherUserProfileFragment, bundle)
            }

            is ShowCommunityDetails -> {
                val bundle = Bundle()
                bundle.putInt("communityId", any.communityData.id)
                bundle.putSerializable("communityData", any.communityData)
                findNavController().navigate(R.id.communityDetailsFragment, bundle)
            }

            is ShowPostDetails -> {
                val bundle = Bundle()
                bundle.putInt("communityId", any.communityId)
                bundle.putSerializable("postId", any.postId)
                findNavController().navigate(R.id.postDetailsFragment, bundle)
            }
        }
    }
}