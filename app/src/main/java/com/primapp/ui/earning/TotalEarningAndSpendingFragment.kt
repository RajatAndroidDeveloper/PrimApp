package com.primapp.ui.earning

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentTotalEarningAndSpendingBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.earning.ContentItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class TotalEarningAndSpendingFragment : BaseFragment<FragmentTotalEarningAndSpendingBinding>() {
    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    lateinit var adapter: TotalEarningSpendingAdapter
    private var earningSpendingList: ArrayList<ContentItem> = ArrayList<ContentItem>()

    override fun getLayoutRes() = R.layout.fragment_total_earning_and_spending

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        if(TotalEarningAndSpendingFragmentArgs.fromBundle(requireArguments()).type == "Earning"){
            setToolbar(getString(R.string.total_earning), toolbar)
        }else{
            setToolbar(getString(R.string.total_spent), toolbar)
        }
        setAdapter(earningSpendingList)
        attachObservers()
        viewModel.getTotalEarnings()
    }

    private fun attachObservers() {
        viewModel.totalEarningLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.SUCCESS -> {
                        if(it.data?.content.isNullOrEmpty()){

                        }else{
                            setAdapter(it.data?.content as ArrayList<ContentItem>)
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

    private fun setAdapter(earningSpendingList: ArrayList<ContentItem>) {
        binding.rvTotalEarningSpending.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setDivider(R.drawable.recyclerview_divider)
        }

        adapter = TotalEarningSpendingAdapter(earningSpendingList)
        binding.rvTotalEarningSpending.adapter = adapter
    }
}