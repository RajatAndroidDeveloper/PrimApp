package com.primapp.ui.communities

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.primapp.R
import com.primapp.databinding.FragmentCommunitiesBinding
import com.primapp.extensions.showError
import com.primapp.model.category.ParentCategoryResult
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.ParentCategoryListAdapter
import com.primapp.viewmodels.CommunitiesViewModel

class CommunitiesFragment : BaseFragment<FragmentCommunitiesBinding>() {

    var isNewUser: Boolean = false

    val adapter by lazy { ParentCategoryListAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

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
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
        isNewUser = CommunitiesFragmentArgs.fromBundle(requireArguments()).isNewUser
    }

    private fun setObserver() {
        viewModel.parentCategoryLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    viewModel.isLoading.value = false
                    showError(requireContext(), it.message!!)
                }

                Status.LOADING -> {
                    viewModel.isLoading.value = true
                }

                Status.SUCCESS -> {
                    viewModel.isLoading.value = false
                    it.data?.content?.categories?.results?.let {
                        adapter.addData(it)
                    }
                }
            }
        })

        viewModel.getParentCategoriesList()
    }

    fun openCommunities(name: String) {
        val bundle = Bundle()
        bundle.putString("title", name)
        bundle.putBoolean("isNewUser", isNewUser)
        findNavController().navigate(R.id.communityJoinViewFragment, bundle)
    }

    private fun onItemClick(item: Any) {
        when (item) {
            is ParentCategoryResult -> {
                openCommunities(item.categoryName)
            }
        }
    }

}