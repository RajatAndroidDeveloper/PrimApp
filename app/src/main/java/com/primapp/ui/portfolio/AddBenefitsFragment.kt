package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.primapp.R
import com.primapp.databinding.FragmentAddBenefitsBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_community_back.*

class AddBenefitsFragment : BaseFragment<FragmentAddBenefitsBinding>() {

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_add_benefits

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.benefits), toolbar)
        setData()
        setObserver()
    }

    fun setData() {
        binding.frag = this

        ivAdd.setOnClickListener {
            DialogUtils.showEditTextDialog(
                requireActivity(),
                getString(R.string.benefits),
                getString(R.string.add_benefit),
                null,
                getString(R.string.add_benefit_here),
                {
                    if (!it.isNullOrEmpty()) {
                        viewModel.addBenefit(it)
                    }
                })
        }
    }

    private fun setObserver() {
        viewModel.addBenefitLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.let {

                        }
                    }
                }
            }
        })
    }
}