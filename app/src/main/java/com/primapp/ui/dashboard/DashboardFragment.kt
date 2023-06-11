package com.primapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.MentorMenteeUserType
import com.primapp.constants.MentorshipStatusTypes
import com.primapp.databinding.FragmentDashboardBinding
import com.primapp.extensions.showError
import com.primapp.model.contract.RatingItem
import com.primapp.model.mentormentee.ResultsItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.contract.RatingFragmentArgs
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class DashboardFragment : BaseFragment<FragmentDashboardBinding>(), MentorsMenteesAdaptor.Callbacks {
    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }
    private var searchJob: Job? = null
    private var adapter: MentorsMenteesAdaptor? = null
    private lateinit var webSocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        webSocketListener = WebSocketListener(viewModel)
        setToolbar(getString(R.string.dashboard), toolbar)
        initTextListeners()
        setObserver()
        getMentorsAndMentees()
        viewModel.getDashboardDetails()

        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        try {
            viewModel.messages.observe(viewLifecycleOwner) {
                mainMentorsMenteeList.clear()
                dummyMentorsMenteeList.clear()
                mainMentorsMenteeList = Gson().fromJson<ArrayList<ResultsItem>>(it.second, object :
                    TypeToken<ArrayList<ResultsItem>>() {}.type)
                loadAdapters(mainMentorsMenteeList)
                Log.e("Connected12", "---" + mainMentorsMenteeList.size + "------   " + it.second)
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun setObserver() {
        viewModel.mentorsMenteesLiveDataNew.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.let { response ->
                            mainMentorsMenteeList.clear()
                            dummyMentorsMenteeList.clear()
                            if (adapter != null) adapter?.notifyDataSetChanged()
                            if (!it.data.content?.results.isNullOrEmpty()) {
                                mainMentorsMenteeList = it.data.content?.results as ArrayList<ResultsItem>
                                loadAdapters(mainMentorsMenteeList)
                            }
                        }
                    }
                }
            }
        })

        viewModel.dashboardDetailsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.let { response ->
                            binding.dashboardData = response
                        }
                    }
                }
            }
        })
    }

    private var mainMentorsMenteeList: ArrayList<ResultsItem> = ArrayList<ResultsItem>()
    private var dummyMentorsMenteeList: ArrayList<ResultsItem> = ArrayList<ResultsItem>()
    private fun loadAdapters(mentorsMenteesData: List<ResultsItem>) {
        if (mentorsMenteesData.size > 6) {
            for (i in 0..6) {
                dummyMentorsMenteeList.add(mainMentorsMenteeList[i])
            }
        } else {
            dummyMentorsMenteeList = mainMentorsMenteeList
        }
        var layoutManager = GridLayoutManager(requireContext(), 4)
        rvMentorsMentees.layoutManager = layoutManager
        adapter = MentorsMenteesAdaptor(dummyMentorsMenteeList)
        if (mentorsMenteesData.size > 6) {
            adapter?.setCallback(this)
            adapter?.setWithFooter(true)
        }
        rvMentorsMentees.adapter = adapter
    }

    private fun getMentorsAndMentees() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getMentorsMenteesDataNew(
                UserCache.getUserId(requireContext()),
                MentorMenteeUserType.MENTEE,
                MentorshipStatusTypes.ACCEPTED,
                0
            )
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
    }

    private fun updateCommunityMembersListFromInput() {
        binding.etSearch.text.trim().let {
            if (!it.isNullOrEmpty()) {
                searchMembers(it.toString())
            } else {
                getMentorsAndMentees()
            }
        }
    }

    private fun searchMembers(query: String) {
        searchJob?.cancel()
        if (!query.isNullOrEmpty()) {
            searchJob = lifecycleScope.launch {
                viewModel.getMentorMenteeMemberSearchListNew(
                    UserCache.getUserId(requireContext()),
                    MentorMenteeUserType.MENTEE,
                    MentorshipStatusTypes.ACCEPTED,
                    0,
                    query
                )
            }
        } else {
            getMentorsAndMentees()
        }
    }

    fun refreshData() {

    }

    fun openCreateContractFragment() {
        var action = DashboardFragmentDirections.actionDashboardFragmentToCreateContractFragment2("dashboard", "")
        findNavController().navigate(action)
    }

    override fun onClickLoadMore() {
        adapter?.setWithFooter(false) // hide footer
        for (i in 6 until mainMentorsMenteeList.size) {
            dummyMentorsMenteeList.add(mainMentorsMenteeList[i])
        }
        adapter?.notifyDataSetChanged() // more elements will be added
    }

    fun btnCurrentProjectAction() {
        findNavController().navigate(R.id.action_dashboardFragment_to_currentProjectsFragment)
    }

    fun btnTotalEarningAction() {
        val action = if (binding.textTotalEarning.text == getString(R.string.total_spent))
            DashboardFragmentDirections.actionDashboardFragmentToTotalEarningAndSpendingFragment("Spending")
        else DashboardFragmentDirections.actionDashboardFragmentToTotalEarningAndSpendingFragment("Earning")

        findNavController().navigate(action)
    }

    fun navigateToRatingFragment() {
        var action = DashboardFragmentDirections.actionDashboardFragmentToRatingFragment("Dashboard", 0, "")
        findNavController().navigate(action)
    }

    private fun createRequest(): Request {
        val websocketURL = "wss://api.prim-technology.com/ws/online-status/?token=${UserCache.getAccessToken(requireContext())}"

        return Request.Builder()
            .url(websocketURL)
            .build()
    }
}