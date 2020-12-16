package com.primapp.ui.communities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.CommunityFilterTypes
import com.primapp.databinding.FragmentCommunityDetailsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.category.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityMembersImageAdapter
import com.primapp.ui.communities.adapter.CommunityPagedListAdapter
import com.primapp.utils.OverlapItemDecorantion
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.item_list_community.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class CommunityDetailsFragment : BaseFragment<FragmentCommunityDetailsBinding>() {

    lateinit var communityData: CommunityData

    val adapter by lazy { CommunityMembersImageAdapter() }

    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }

    val userData by lazy { UserCache.getUser(requireContext()) }

    override fun getLayoutRes(): Int = R.layout.fragment_community_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setObserver()
        setData()
        setAdapter()
        setClicks()

    }

    private fun setData() {
        CommunityDetailsFragmentArgs.fromBundle(requireArguments()).apply {
            viewModel.getCommunityDetails(communityId)
        }

        binding.type = CommunityFilterTypes.COMMUNITY_DETAILS
    }

    private fun setObserver() {
        viewModel.getCommunityDetailsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                hideLoading()
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.content?.apply {
                            communityData = this
                            binding.data = communityData
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), response.message!!)
                        findNavController().popBackStack()
                    }
                }
            }
        })

        viewModel.joinCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                hideLoading()
                when (response.status) {
                    Status.SUCCESS -> {
                        communityData.isJoined = response.data?.content?.isJoined
                        binding.data = communityData
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), response.message!!)
                    }
                }
            }
        })
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.rvMembersImages.apply {
            this.layoutManager = layoutManager
            addItemDecoration(OverlapItemDecorantion())

        }
        binding.rvMembersImages.adapter = adapter
    }

    private fun setClicks() {
        btnJoin.setOnClickListener {
            if (communityData.isCreatedByMe == true) {
                showInfo(requireContext(), "Not yet implemented.")
            } else {
                if (communityData.isJoined == true) {
                    viewModel.leaveCommunity(communityData.id, userData!!.id)
                } else {
                    viewModel.joinCommunity(communityData.id, userData!!.id)
                }
            }
        }
    }


}