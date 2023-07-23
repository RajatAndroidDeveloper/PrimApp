package com.primapp.ui.notification

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.databinding.FragmentNotificationsBinding
import com.primapp.extensions.showError
import com.primapp.model.*
import com.primapp.model.mentormentee.ResultsItem
import com.primapp.model.notification.NotificationUIModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.ui.dashboard.WebSocketListener
import com.primapp.ui.notification.adapter.NotificationsPagedAdapter
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.utils.checkIsNetworkConnected
import com.primapp.viewmodels.CommunitiesViewModel
import com.primapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private var notificationJob: Job? = null

    private var requestId: Int? = null
    private var notificationFilterType: String? = null

    val adapter by lazy { NotificationsPagedAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<NotificationViewModel> { viewModelFactory }
    val mViewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_notifications

    private var webSocket: WebSocket? = null
    private lateinit var webSocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("Notifications", toolbar)

        webSocketListener = WebSocketListener(mViewModel)
        setData()
        setAdapter()
        setObserver()

        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        activityScope.launch {
            delay(1500)
            findOnlineOfflineStatus()
        }
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

        if (checkIsNetworkConnected(requireContext())) {
            //get notification for first time
            getNotification(notificationFilterType)
        } else {
            findNavController().navigate(R.id.networkErrorFragment)
        }
    }

    private fun getNotification(filter: String?) {
        // Make sure we cancel the previous job before creating a new one

//        notificationJob?.cancel()
//        notificationJob = lifecycleScope.launch {
//            viewModel.getUserNotification(notificationFilterType).observe(viewLifecycleOwner, Observer {
//                adapter.submitData(lifecycle, it)
//            })
//        }
    }

    private fun setObserver() {
        viewModel.getUserNotification(notificationFilterType).observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            }
        })

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

            binding.tvNoData.isVisible =
                loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1
        }
    }

    fun refreshData() {
        if (checkIsNetworkConnected(requireContext()))
            adapter.refresh()
        else
            findNavController().navigate(R.id.networkErrorFragment)
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

            is EditUploadedPost -> {
                val bundle = Bundle()
                bundle.putString("type","upload_virus_free_data")
                bundle.putInt("communityId", any.communityId)
                bundle.putInt("postId", any.postId)
                findNavController().navigate(R.id.createPostFragment, bundle)
            }

            is ShowContractDetails -> {
                val bundle = Bundle()
                bundle.putInt("contract_id", any.contractId)
                findNavController().navigate(R.id.projectDetailsFragment, bundle)
            }
        }
    }

    private fun createRequest(): Request {
        val websocketURL = "wss://api.prim-technology.com/ws/online-status/?token=${UserCache.getAccessToken(requireContext())}"

        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    private fun findOnlineOfflineStatus(){
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        mViewModel.messages.observe(requireActivity()) {
            var mainMentorsMenteeList = ArrayList<ResultsItem>()
            mainMentorsMenteeList.clear()
            mainMentorsMenteeList = Gson().fromJson<ArrayList<ResultsItem>>(it.second, object :
                TypeToken<ArrayList<ResultsItem>>() {}.type)
            mergeListAndAdapterData(mainMentorsMenteeList)
        }
    }
    private fun mergeListAndAdapterData(mainMentorsMenteeList: java.util.ArrayList<ResultsItem>) {
        var data = adapter.snapshot().items
        for(i in mainMentorsMenteeList.indices){
            for(j in 0 until (data.size)){
                if(data[j] is NotificationUIModel.NotificationItem) {
                    if (mainMentorsMenteeList[i].id == (data.get(j) as NotificationUIModel.NotificationItem).notification.sender?.id) {
                        (data[j] as NotificationUIModel.NotificationItem).notification.sender?.userOnlineStatus = mainMentorsMenteeList[i].userOnlineStatus
                    }
                }
            }
        }
        lifecycleScope.launch {
            adapter.submitData(PagingData.from(data))
        }
    }
}