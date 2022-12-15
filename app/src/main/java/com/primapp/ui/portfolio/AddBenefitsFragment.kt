package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentAddBenefitsBinding
import com.primapp.extensions.showError
import com.primapp.model.DeleteItem
import com.primapp.model.EditBenefits
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.AddBenefitsAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_community_back.*

class AddBenefitsFragment : BaseFragment<FragmentAddBenefitsBinding>() {

    lateinit var portfolioContent: PortfolioContent

    private val adapter by lazy { AddBenefitsAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_add_benefits

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.benefits), toolbar)
        setAdapter()
        setData()
        setObserver()
    }

    private fun setAdapter() {
        binding.rvBenefits.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvBenefits.adapter = adapter
    }

    fun setData() {
        binding.frag = this
        portfolioContent = AddBenefitsFragmentArgs.fromBundle(requireArguments()).portfolioData

        portfolioContent.benefits?.let {
            adapter.addData(it)
        }

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
                            adapter.addBenefit(it.content)
                            //Update list to avoid api call
                            portfolioContent.benefits = adapter.list
                        }
                    }
                }
            }
        })

        viewModel.updateBenefitsLiveData.observe(viewLifecycleOwner, Observer {
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
                            adapter.updateBenefit(it.content)
                            //Update list to avoid api call
                            portfolioContent.benefits = adapter.list
                        }
                    }
                }
            }
        })

        viewModel.deleteBenefitsLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            adapter.deleteBenefit(it.id)
                            //Update list to avoid api call
                            portfolioContent.benefits = adapter.list
                        }
                    }
                }
            }
        })
    }


    private fun onItemClick(item: Any?) {
        when (item) {
            is EditBenefits -> {
                DialogUtils.showEditTextDialog(
                    requireActivity(),
                    getString(R.string.benefits),
                    getString(R.string.edit_benefit),
                    item.benefitData.name,
                    getString(R.string.add_benefit_here),
                    {
                        if (!it.isNullOrEmpty() && !it.equals(item.benefitData.name)) {
                            viewModel.updateBenefit(item.benefitData.id, it)
                        }
                    })
            }

            is DeleteItem -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_benefit_msg, {
                    viewModel.deleteBenefit(item.id)
                })
            }
        }
    }
}