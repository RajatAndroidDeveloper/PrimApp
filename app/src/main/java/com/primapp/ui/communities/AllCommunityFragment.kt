package com.primapp.ui.communities

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.constants.CommunityFilterTypes
import com.primapp.databinding.FragmentAllCommunityBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunitListAdapter
import com.primapp.ui.communities.adapter.CommunityPagedListAdapter
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AllCommunityFragment : BaseFragment<FragmentAllCommunityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_all_community

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    private var searchJob: Job? = null

    val adapter by lazy { CommunityPagedListAdapter() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
        setObserver()
        initTextListeners()
    }

    private fun setData() {
        binding.frag = this
        searchCommunity(null)
    }

    private fun setObserver() {

    }

    private fun searchCommunity(query: String?) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getCommunityListData(
                (parentFragment as CommunityJoinViewFragment).parentCategoryId,
                query,
                CommunityFilterTypes.ALL_COMMUNITY
            ).observe(viewLifecycleOwner, Observer {
                adapter.submitData(lifecycle, it)
            })
        }
    }

    private fun setAdapter() {

        binding.rvCommunityList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        binding.rvCommunityList.adapter = adapter.withLoadStateHeaderAndFooter(
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

            }else{
                binding.swipeRefresh.isRefreshing = true
            }
        }

    }

    fun onItemClick(any: Any) {

    }

    fun refreshData() {
        adapter.refresh()
    }

    private fun initTextListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateCommunityListFromInput()
                true
            } else {
                false
            }
        }
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateCommunityListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateCommunityListFromInput() {
        binding.etSearch.text.trim().let {
            if (it.isNotEmpty()) {
                searchCommunity(it.toString())
            }else{
                searchCommunity(null)
            }
        }
    }
}