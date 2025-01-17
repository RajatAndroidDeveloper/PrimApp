package com.primapp.ui.communities

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
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
import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.checkIsNetworkConnected
import com.primapp.utils.visible
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.activity_dashboard.drawerLayout
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.ivEndIcon
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.ivMenu
import kotlinx.android.synthetic.main.toolbar_dashboard_accent.toolbar
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
        if (checkIsNetworkConnected(requireContext())) {
            setObserver()
        } else {
            findNavController().navigate(R.id.networkErrorFragment)
        }
    }

    private fun setAdapter() {

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
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_COMMUNITIES)
        binding.frag = this
        binding.viewModel = viewModel
        isNewUser = CommunitiesFragmentArgs.fromBundle(requireArguments()).isNewUser

        ivEndIcon.visible(false)
        ivMenu.visible(true)

        setToolbar(getString(R.string.communities), toolbar)
        ivMenu.setOnClickListener {
            (requireActivity() as DashboardActivity).drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setObserver() {
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
        if (checkIsNetworkConnected(requireContext()))
            adapter.refresh()
        else
            findNavController().navigate(R.id.networkErrorFragment)
    }

}