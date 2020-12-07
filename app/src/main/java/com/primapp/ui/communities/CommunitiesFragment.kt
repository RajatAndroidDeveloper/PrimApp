package com.primapp.ui.communities

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentCommunitiesBinding
import com.primapp.extensions.showError
import com.primapp.model.category.ParentCategoryResult
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityPagedLoadStateAdapter
import com.primapp.ui.communities.adapter.ParentCategoryListAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.coroutines.launch

class CommunitiesFragment : BaseFragment<FragmentCommunitiesBinding>() {

    var isNewUser: Boolean = false

    val adapter by lazy { ParentCategoryListAdapter { item -> onItemClick(item) } }

    val viewModel by activityViewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_communities

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
        setObserver()
    }

    private fun setAdapter() {
        val recyclerVeiw = binding.rvParentCategoryList
        recyclerVeiw.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerVeiw.adapter = adapter

        binding.rvParentCategoryList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }

        binding.rvParentCategoryList.adapter = adapter.withLoadStateHeaderAndFooter(
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

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
        isNewUser = CommunitiesFragmentArgs.fromBundle(requireArguments()).isNewUser
    }

    private fun setObserver() {
//        viewModel.parentCategoryLiveData.observe(viewLifecycleOwner, Observer {
//            when (it.status) {
//                Status.ERROR -> {
//                    viewModel.isLoading.value = false
//                    showError(requireContext(), it.message!!)
//                }
//
//                Status.LOADING -> {
//                    viewModel.isLoading.value = true
//                }
//
//                Status.SUCCESS -> {
//                    viewModel.isLoading.value = false
//                    it.data?.content?.results?.let {
//                        adapter.addData(it)
//                    }
//                }
//            }
//        })
//
//        viewModel.getParentCategoriesList(0, ApiConstant.NETWORK_PAGE_SIZE)

        viewModel.getParentCategoriesListData().observe(viewLifecycleOwner, Observer {
            it?.let {
                lifecycleScope.launch {
                    adapter.submitData(it)
                }
            }
        })
    }

    private fun openCommunities(name: String, id: Int) {
        val bundle = Bundle()
        bundle.putString("title", name)
        bundle.putBoolean("isNewUser", isNewUser)
        bundle.putInt("parentCategoryId", id)
        findNavController().navigate(R.id.communityJoinViewFragment, bundle)
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is ParentCategoryResult -> {
                openCommunities(item.categoryName, item.id)
            }
        }
    }

    fun refreshData() {
        adapter.refresh()
    }

}