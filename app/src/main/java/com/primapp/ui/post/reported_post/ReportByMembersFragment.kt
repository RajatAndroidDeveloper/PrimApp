package com.primapp.ui.post.reported_post

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentReportByMembersBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.post.reported_post.adapter.ReportedMembersAdapter
import com.primapp.viewmodels.PostsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class ReportByMembersFragment : BaseFragment<FragmentReportByMembersBinding>() {

    private var communityId: Int? = null
    private var postId: Int? = null

    val adapter by lazy { ReportedMembersAdapter() }

    val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_report_by_members

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.reports), toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        communityId = ReportByMembersFragmentArgs.fromBundle(requireArguments()).communityId
        postId = ReportByMembersFragmentArgs.fromBundle(requireArguments()).postId

        binding.frag = this
        refreshData()
    }

    private fun setObserver() {
        viewModel.reportedMembersLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            adapter.addData(it)
                        }
                    }
                }
            }
        })
    }

    private fun setAdapter() {
        binding.rvReportedMembers.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvReportedMembers.adapter = adapter
    }

    fun refreshData() {
        viewModel.reportedPostMembers(communityId!!, postId!!)
    }
}