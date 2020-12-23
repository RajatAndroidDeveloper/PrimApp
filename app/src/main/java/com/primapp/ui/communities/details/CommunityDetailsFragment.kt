package com.primapp.ui.communities.details

import android.os.Bundle
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
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.communities.adapter.CommunityMembersImageAdapter
import com.primapp.utils.DialogUtils
import com.primapp.utils.OverlapItemDecorantion
import com.primapp.utils.visible
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
        CommunityDetailsFragmentArgs.fromBundle(requireArguments())
            .let { args ->
                viewModel.getCommunityDetails(args.communityId)
                communityData = args.communityData
            }

        binding.type = CommunityFilterTypes.COMMUNITY_DETAILS
        binding.data = communityData
    }

    private fun setObserver() {
        viewModel.getCommunityDetailsLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        showAdditionalData(true)
                        response.data?.content?.apply {
                            communityData.isJoined = isJoined
                            communityData.communityName = communityName
                            communityData.communityDescription = communityDescription
                            communityData.communityImageFile = communityImageFile
                            communityData.status = status
                            communityData.udate = udate
                            communityData.totalActiveMember = totalActiveMember
                            binding.data = communityData
                        }
                    }
                    Status.LOADING -> {
                        showAdditionalData(false)
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
                        response.data?.content?.apply {
                            communityData.isJoined = isJoined
                            communityData.totalActiveMember = totalActiveMember
                        }

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

    private fun showAdditionalData(visibility: Boolean) {
        // Show progressbar if additional data is not visible
        binding.pbAdditionalData.visible(!visibility)
        binding.llAdditionalData.visible(visibility)
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
                val bundle = Bundle()
                bundle.putSerializable("communityData", communityData)
                findNavController().navigate(R.id.editCommunityFragment, bundle)
            } else {
                if (communityData.isJoined == true) {
                    DialogUtils.showYesNoDialog(
                        requireActivity(),
                        R.string.leave_Community_message,
                        yesClickCallback = {
                            viewModel.leaveCommunity(communityData.id, userData!!.id)
                        })
                } else {
                    viewModel.joinCommunity(communityData.id, userData!!.id)
                }
            }
        }
    }


}