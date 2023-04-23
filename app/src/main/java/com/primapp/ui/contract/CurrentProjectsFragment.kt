package com.primapp.ui.contract

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentCurrentProjectsBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.mycontracts.CompletedContractsItem
import com.primapp.model.mycontracts.OngoingContractsItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.contract.adapters.MyContractWithoutFilterAdapter
import com.primapp.ui.contract.adapters.OnContractClickEvent
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CurrentProjectsFragment : BaseFragment<FragmentCurrentProjectsBinding>(), OnContractClickEvent {
    override fun getLayoutRes() = R.layout.fragment_current_projects
    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        setToolbar(getString(R.string.created_contracts), toolbar)
        attachObservers()
        viewModel.getMyOwnContractsWithoutFilter()
    }

    private fun attachObservers() {
        viewModel.myContractsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.SUCCESS -> {
                        if((it.data?.content?.results?.completedContracts?.size ?: 0) > 0 || (it.data?.content?.results?.ongoingContracts?.size ?: 0) > 0) {
                            binding.tvNoData.isVisible = false
                            setUpDataAdapters(it.data?.content?.results?.ongoingContracts as ArrayList<OngoingContractsItem>, it.data?.content?.results?.completedContracts as ArrayList<CompletedContractsItem>)
                        } else {
                            binding.tvNoData.isVisible = true
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })
    }

    private fun setUpDataAdapters(ongoingContractsItems: ArrayList<OngoingContractsItem>, completedContractsItems: ArrayList<CompletedContractsItem>) {
        if(ongoingContractsItems.size == 0) binding.tvOngoingProjects.isVisible = false
        if(completedContractsItems.size == 0) binding.tvCompletedProjects.isVisible = false

        var layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrentProjects.layoutManager = layoutManager
        binding.rvCurrentProjects.setDivider(R.drawable.recyclerview_divider)
        var ongoingAdapter = MyContractWithoutFilterAdapter(requireActivity(), ongoingContractsItems, completedContractsItems, "ongoing", this)
        binding.rvCurrentProjects.adapter = ongoingAdapter

        var layoutManager1 = LinearLayoutManager(requireContext())
        binding.rvCompletedProjects.layoutManager = layoutManager1
        binding.rvCompletedProjects.setDivider(R.drawable.recyclerview_divider)
        var completedAdapter = MyContractWithoutFilterAdapter(requireActivity(), ongoingContractsItems, completedContractsItems, "completed", this)
        binding.rvCompletedProjects.adapter = completedAdapter
    }

    fun refreshData() {
        viewModel.getMyOwnContractsWithoutFilter()
    }

    override fun onContractItemClickEvent(contractType: String, contractId: Int) {
        val action = CurrentProjectsFragmentDirections.actionCurrentProjectsFragmentToProjectDetailsFragment(contractId)
        findNavController()?.navigate(action)
    }

    fun navigateToAllProjectsFragment(contractType: String){
        val action = CurrentProjectsFragmentDirections.actionCurrentProjectsFragmentToAllProjectsFragment("MyContracts",contractType)
        findNavController().navigate(action)
    }
}