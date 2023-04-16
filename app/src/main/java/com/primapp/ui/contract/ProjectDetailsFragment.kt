package com.primapp.ui.contract

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProjectDetailsBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.contract.AmendRequestItem
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_menu_more.*

class ProjectDetailsFragment : BaseFragment<FragmentProjectDetailsBinding>(), OnButtonClickCallback {

    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    override fun getLayoutRes() = R.layout.fragment_project_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        binding.frag = this
        binding.user = UserCache.getUser(requireContext())
        binding.tvAboutDescription.text = getString(R.string.project_scope_dummy_text)
        binding.tvAboutDescription.setTrimLength(60)

        attachObservers()
        viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
    }

    private fun attachObservers() {
        viewModel.contractDetailLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.data = it.data?.content
                        if (!it.data?.content?.amendRequest.isNullOrEmpty()) {
                            setUpAmendRequestAdapter(it.data?.content?.amendRequest as ArrayList<AmendRequestItem>)
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                            findNavController().popBackStack()
                        }
                    }
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                        showLoading()
                    }
                }
            }
        })

        viewModel.acceptContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_accepted_successfully, R.drawable.correct
                        ) {
                            viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
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

        viewModel.acceptAmendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        refreshData()
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
        viewModel.amendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.llAmendReqyestLayout.llMainLayout.visibility = View.GONE
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_amend_request_sent_successfully, R.drawable.correct
                        ) {
                            refreshData()
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

    private fun setUpAmendRequestAdapter(amendRequestItems: java.util.ArrayList<AmendRequestItem>) {
        var layoutManager = LinearLayoutManager(requireContext())
        binding.rvAmendRequest.layoutManager = layoutManager
        binding.rvAmendRequest.setDivider(R.drawable.recyclerview_divider)
        var amendRequestAdapter = AmendRequestAdapter(amendRequestItems, requireContext(), this)
        binding.rvAmendRequest.adapter = amendRequestAdapter
    }

    fun acceptContract(buttonType: String) {
        when (buttonType) {
            "Accept" -> {
                val model = viewModel.acceptContractRequestModel.value
                model?.status = ApiConstant.CONTRACT_ACCEPTED
                model?.acceptedPrice = (binding.data?.price ?: "0.0").toDouble()
                model?.contract = binding.data?.id
                viewModel.validContractAcceptData()
            }
            "Start Contract" -> {

            }
        }
    }

    fun navigateToAmendDialog(buttonType: String) {
        when (buttonType) {
            "Amend" -> {
                binding.llAmendReqyestLayout.llMainLayout.visibility = View.VISIBLE
                binding.llAmendReqyestLayout.viewModel = viewModel
                binding.llAmendReqyestLayout.frag = this
            }
            "End Contract" -> {

            }
        }
    }

    fun amendContract() {
        val model = viewModel.amendContractRequestModel.value
        model?.contract = ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId
        var amount = if (binding.llAmendReqyestLayout.etPrice.text.toString().isEmpty()) 0.0 else binding.llAmendReqyestLayout.etPrice.text?.trim().toString().toDouble()
        model?.amount =  String.format("%.2f", amount).toDouble()
        viewModel.validateContractAmendData()
    }

    fun closeDialog() {
        binding.llAmendReqyestLayout.llMainLayout.visibility = View.GONE
    }

    fun refreshData(){
        viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
    }

    override fun onButtonCLickCallback(data: AmendRequestItem, type: String) {
        if(type == "PAY_NOW") {
            val model = viewModel.acceptContractRequestModel.value
            model?.status = ApiConstant.CONTRACT_ACCEPTED
            model?.acceptedPrice = (data.amount?:"0.0").toDouble()
            model?.contract = binding.data?.id
            viewModel.validContractAcceptData()
        }else {
            val model = viewModel.acceptAmendRequestModel.value
            model?.status = type
            viewModel.acceptDeclineAmendRequest(data.id ?: 0)
        }
    }
}