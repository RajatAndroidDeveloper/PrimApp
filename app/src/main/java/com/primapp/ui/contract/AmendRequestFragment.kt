package com.primapp.ui.contract

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentAmendRequestBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_menu_more.*


class AmendRequestFragment : BaseFragment<FragmentAmendRequestBinding>() {
    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }

    override fun getLayoutRes() = R.layout.fragment_amend_request

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
        setToolbar(getString(R.string.select_amend_reason), toolbar)
        setUpRadioGroupListener()
        attachObservers()
        editTextFocusListener()
    }

    private fun setUpRadioGroupListener() {
        binding.rgAmendReason.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val model = viewModel.amendContractRequestModel.value
            when (checkedId) {
                R.id.rbCostIsHigh -> {
                    model?.reason = getString(R.string.cost_is_too_high)
                    binding.etSomethingElseReason.isVisible = false
                }
                R.id.rbOutOfScope -> {
                    model?.reason = getString(R.string.out_of_scope)
                    binding.etSomethingElseReason.isVisible = false
                }
                R.id.rbWordingChanges -> {
                    model?.reason = getString(R.string.contract_wording_changes)
                    binding.etSomethingElseReason.isVisible = false
                }
                else -> {
                    model?.reason = getString(R.string.something_else)
                    binding.etSomethingElseReason.isVisible = true
                }
            }
        })
    }

    private fun attachObservers(){
        viewModel.amendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_amend_request_sent_successfully, R.drawable.correct
                        ) {
                            findNavController().popBackStack()
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

    fun editTextFocusListener(){
        binding.etPrice.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etPrice.setText("$")
            }
        }
    }

    fun amendContract() {
        val model = viewModel.amendContractRequestModel.value
        model?.contract = ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId

        if(model?.reason.equals(getString(R.string.something_else)) && binding.etSomethingElseReason.text.trim().isEmpty()){
            showError(requireContext(),getString(R.string.please_enter_other_contract_amendment_reason))
            return
        } else {
            model?.reason = binding.etSomethingElseReason.text.toString().trim()
        }

        var enteredAmount = binding.etPrice.text.toString().trim().replace("$","")
        var amount = if (enteredAmount.isEmpty()) 0.0 else enteredAmount.toDouble()
        model?.amount = String.format("%.2f", amount).toDouble()
        viewModel.validateContractAmendData()
    }
}
