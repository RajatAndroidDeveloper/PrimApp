package com.primapp.ui.dashboard

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.MentorMenteeUserType
import com.primapp.constants.MentorshipStatusTypes
import com.primapp.databinding.FragmentDashboardBinding
import com.primapp.extensions.showError
import com.primapp.model.members.CommunityMembersData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.earning.TotalEarningAndSpendingFragment
import com.primapp.ui.initial.PasswordVerificationFragment
import com.primapp.ui.initial.VerifyOTPFragmentDirections
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(), MentorsMenteesAdaptor.Callbacks, AdapterView.OnItemSelectedListener {

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }
    private var searchJob: Job? = null
    private var adapter: MentorsMenteesAdaptor ?= null
    private var selectedCategory: String = ""

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        setToolbar(getString(R.string.dashboard), toolbar)
        initTextListeners()
        setUpSpinnerData()
        setObserver()
    }

    private fun setObserver() {
        viewModel.mentorsMenteesLiveData.observe(viewLifecycleOwner, Observer {
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
                            if(adapter != null) adapter?.notifyDataSetChanged()
                            if(it.data.content.results.isNotEmpty()) {
                                mainMentorsMenteeList = it.data.content.results as ArrayList<CommunityMembersData>
                                loadAdapters(mainMentorsMenteeList)
                            }
                        }
                    }
                }
            }
        })
    }

    private var mainMentorsMenteeList: ArrayList<CommunityMembersData> = ArrayList<CommunityMembersData>()
    private var dummyMentorsMenteeList: ArrayList<CommunityMembersData> = ArrayList<CommunityMembersData>()
    private fun loadAdapters(mentorsMenteesData: List<CommunityMembersData>) {
        if(mentorsMenteesData.size>6){
            for (i in 0..6) {
                dummyMentorsMenteeList.add(mainMentorsMenteeList[i])
            }
        }else{
            dummyMentorsMenteeList = mainMentorsMenteeList
        }
        var layoutManager = GridLayoutManager(requireContext(), 4)
        rvMentorsMentees.layoutManager = layoutManager
        adapter = MentorsMenteesAdaptor(dummyMentorsMenteeList)
        if(mentorsMenteesData.size>6) {
            adapter?.setCallback(this)
            adapter?.setWithFooter(true)
        }
        rvMentorsMentees.adapter = adapter
    }

    private fun getColoredText(text: String): String{
        return "<font color='#0085FF'>$text </font>"
    }

    private lateinit var categoriesArray: Array<String>
    private fun setUpSpinnerData() {
        categoriesArray= requireActivity().resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(requireActivity(), R.layout.custom_spinner_item_layout, categoriesArray)
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.onItemSelectedListener = this
    }

    private fun getMentorsAndMentees(type: String) {
        searchJob?.cancel()
        searchJob = when (selectedCategory) {
            "Mentee" -> {
                lifecycleScope.launch {
                    viewModel.getMentorsMenteesData(
                        UserCache.getUserId(requireContext()),
                        MentorMenteeUserType.MENTOR,
                        MentorshipStatusTypes.ACCEPTED,
                        0
                    )
                }
            }
            else -> {
                lifecycleScope.launch {
                    viewModel.getMentorsMenteesData(
                        UserCache.getUserId(requireContext()),
                        MentorMenteeUserType.MENTEE,
                        MentorshipStatusTypes.ACCEPTED,
                        0
                    )
                }
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
    }

    private fun updateCommunityMembersListFromInput() {
        binding.etSearch.text.trim().let {
            if (!it.isNullOrEmpty()) {
                searchMembers(it.toString())
            }else{
                getMentorsAndMentees(selectedCategory)
            }
        }
    }

    private fun searchMembers(query: String) {
        searchJob?.cancel()
        if(!query.isNullOrEmpty()) {
            searchJob = when (selectedCategory) {
                "Mentee" -> {
                    lifecycleScope.launch {
                        viewModel.getMentorMenteeMemberSearchList(
                            UserCache.getUserId(requireContext()),
                            MentorMenteeUserType.MENTOR,
                            MentorshipStatusTypes.ACCEPTED,
                            0,
                            query)
                    }
                }
                else -> {
                    lifecycleScope.launch {
                        viewModel.getMentorMenteeMemberSearchList(
                            UserCache.getUserId(requireContext()),
                            MentorMenteeUserType.MENTEE,
                            MentorshipStatusTypes.ACCEPTED,
                            0,
                            query
                        )
                    }
                }
            }
        } else{
            getMentorsAndMentees(selectedCategory)
        }
    }

    fun refreshData() {

    }

    fun openCreateContractFragment(){
        findNavController().navigate(R.id.action_dashboardFragment_to_createContractFragment2)
    }

    override fun onClickLoadMore() {
        adapter?.setWithFooter(false) // hide footer
        for (i in 6 until mainMentorsMenteeList.size) {
            dummyMentorsMenteeList.add(mainMentorsMenteeList[i])
        }
        adapter?.notifyDataSetChanged() // more elements will be added
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedCategory = categoriesArray[p2]
        if (selectedCategory == "Mentor") {
            binding.btnCurrentProjects.text = requireContext().resources.getString(R.string.current_projects)
            binding.btnCreateContact.text = requireContext().resources.getString(R.string.create_contracts)
            binding.textTotalSpent.text = requireContext().resources.getString(R.string.total_earning)
            binding.textTotalEarnings.text = requireContext().resources.getString(R.string.ratings)
            binding.tvMemberTitle.text = requireContext().resources.getString(R.string.your_mentees)
            binding.textTotalEarnVal.text = "4.5/5"
            binding.textServed.text = requireContext().resources.getString(R.string.mentees_served)+" 40"
            binding.descriptionText.text = (Html.fromHtml(requireContext().resources.getString(R.string.request_money_from_mentees) + " " + getColoredText(requireContext().resources.getString(R.string.mentees))))
        } else {
            binding.btnCurrentProjects.text = requireContext().resources.getString(R.string.view_projects)
            binding.btnCreateContact.text = requireContext().resources.getString(R.string.view_contracts)
            binding.textTotalSpent.text = requireContext().resources.getString(R.string.total_spent)
            binding.textTotalEarnings.text = requireContext().resources.getString(R.string.amount_due)
            binding.tvMemberTitle.text = requireContext().resources.getString(R.string.your_mentors)
            binding.textTotalEarnVal.text = "$1200.00"
            binding.textServed.text = requireContext().resources.getString(R.string.my_mentors)+" 4"
            binding.descriptionText.text = (Html.fromHtml(requireContext().resources.getString(R.string.pay_money_to_mentors) + " " + getColoredText(requireContext().resources.getString(R.string.mentors))))
        }
        getMentorsAndMentees(selectedCategory)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun btnCurrentProjectAction(){
        if(selectedCategory == "Mentor"){
            findNavController().navigate(R.id.action_dashboardFragment_to_currentProjectsFragment)
        }
    }
    fun btnTotalEarningAction(){
        val action = if(binding.textTotalSpent.text == getString(R.string.total_spent))
        DashboardFragmentDirections.actionDashboardFragmentToTotalEarningAndSpendingFragment("Spending")
        else DashboardFragmentDirections.actionDashboardFragmentToTotalEarningAndSpendingFragment("Earning")

        findNavController().navigate(action)
    }
}