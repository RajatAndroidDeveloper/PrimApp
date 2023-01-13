package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentUsersCommunityListBinding
import com.primapp.extensions.showError
import com.primapp.model.RequestMentorWithCommunityId
import com.primapp.model.mentor.RequestMentorDataModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.CommonCommunitiesListAdapter
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class UsersCommunityListFragment : BaseFragment<FragmentUsersCommunityListBinding>() {

    var userId: Int? = null

    private val adapter by lazy { CommonCommunitiesListAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_users_community_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.communities), toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        userId = UsersCommunityListFragmentArgs.fromBundle(requireArguments()).userId

        viewModel.getUserCommonCommunities(userId!!)
    }

    private fun setAdapter() {
        binding.rvUsersCommunity.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvUsersCommunity.adapter = adapter
    }

    private fun setObserver() {
        viewModel.commonCommunitesLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.commonCommunityList?.let {
                            adapter.addData(it)
                        }
                    }
                }
            }
        })

        viewModel.requestMentorLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.content?.community?.let {
                            adapter.markRequestAsSent(it.id)
                        }
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

    private fun onItemClick(item: Any?) {
        when (item) {
            is RequestMentorWithCommunityId -> {
                viewModel.requestMentor(
                    item.communityId, UserCache.getUserId(requireContext()),
                    RequestMentorDataModel(userId!!, null)
                )
            }
        }
    }
}