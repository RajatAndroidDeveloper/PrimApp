package com.primapp.ui.contract

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentAllProjectsBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.contract.ResultsItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.post.adapter.PostListPagedAdapter
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import kotlinx.coroutines.launch

class AllProjectsFragment : BaseFragment<FragmentAllProjectsBinding>(), AdapterView.OnItemSelectedListener{

    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    val adapter by lazy { CurrentProjectsAdapter { item -> onItemClick(item) } }
    override fun getLayoutRes() = R.layout.fragment_all_projects
    private var contractType: String = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
        setToolbar(getString(R.string.all_projects), toolbar)
        setUpStatusSpinner()
        setAdapter()
    }

    private lateinit var statusArray: Array<String>
    private fun setUpStatusSpinner() {
        statusArray= requireActivity().resources.getStringArray(R.array.status)
        val adapter = ArrayAdapter(requireActivity(), R.layout.custom_spinner_item_layout, statusArray)
        binding.statusSpinner.adapter = adapter
        binding.statusSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        contractType = statusArray[p2]
        when(contractType) {
            "All Contracts"-> attachObservers("all_projects")
            "Ongoing Contracts"-> attachObservers("ongoing")
            "Completed"-> attachObservers("completed")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun setAdapter() {
        binding.rvAllProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvAllProjects.adapter = adapter.withLoadStateHeaderAndFooter(
            header = CommunityPagedLoadStateAdapter { adapter.retry() },
            footer = CommunityPagedLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh !is LoadState.Loading) {
                binding.swipeRefresh.isRefreshing = false

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
            }
        }
    }

    private fun attachObservers(contractType: String) {
        viewModel.getAllContracts(contractType).observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            }
        })
    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun onItemClick(contractId: Int) {
        val action = AllProjectsFragmentDirections.actionAllProjectsFragmentToProjectDetailsFragment(contractId)
        findNavController()?.navigate(action)
    }
}