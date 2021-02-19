package com.primapp.ui.notification

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.databinding.FragmentNotificationsBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.AcceptMetorRequest
import com.primapp.model.RejectMetorRequest
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.notification.adapter.NotificationsPagedAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*
import kotlinx.coroutines.launch

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private var requestId: Int? = null

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
        ivEndIcon.setImageResource(R.drawable.filter)
        binding.frag = this
    }

    private fun setObserver() {
        viewModel.getUserNotification().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

        viewModel.acceptRejectMentorshipLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        adapter.updateRequestAsAccepted(requestId)
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
                findNavController().navigate(R.id.mentorRequestRejectionFragment, bundle)
            }
        }
    }
}