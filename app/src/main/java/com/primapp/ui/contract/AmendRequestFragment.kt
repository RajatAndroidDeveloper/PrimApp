package com.primapp.ui.contract

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
    private var selectedReason: String = ""

    override fun getLayoutRes() = R.layout.fragment_amend_request


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
        setUpRadioGroupListener()
        binding.amendRequestStatus = AmendRequestFragmentArgs.fromBundle(requireArguments()).status
        if(AmendRequestFragmentArgs.fromBundle(requireArguments()).status == "DECLINED" || AmendRequestFragmentArgs.fromBundle(requireArguments()).status == "ACCEPTED"){
            setToolbar(getString(R.string.select_a_reason), toolbar)
        }else{
            setToolbar(getString(R.string.select_amend_reason), toolbar)
        }
        attachObservers()
        editTextFocusListener()

        binding.etSomethingElseReason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvTotalLength.text = "${s?.length}/200"
            }
        })
    }

    private fun setUpRadioGroupListener() {
        binding.rgAmendReason.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbCostIsHigh -> {
                    selectedReason = getString(R.string.cost_is_too_high)
                    binding.llReason.isVisible = false
                }
                R.id.rbOutOfScope -> {
                    selectedReason = getString(R.string.out_of_scope)
                    binding.llReason.isVisible = false
                }
                R.id.rbWordingChanges -> {
                    selectedReason = getString(R.string.contract_wording_changes)
                    binding.llReason.isVisible = false
                }
                else -> {
                    selectedReason = getString(R.string.something_else)
                    binding.llReason.isVisible = true
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

        viewModel.acceptAmendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        findNavController().popBackStack()
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
        if(AmendRequestFragmentArgs.fromBundle(requireArguments()).status == "DECLINED" || AmendRequestFragmentArgs.fromBundle(requireArguments()).status == "ACCEPTED"){
            val model = viewModel.acceptAmendRequestModel.value
            model?.actionReason = selectedReason
            model?.status = AmendRequestFragmentArgs.fromBundle(requireArguments()).status
            viewModel.acceptDeclineAmendRequest(AmendRequestFragmentArgs.fromBundle(requireArguments()).contractId)
        }else {
            val model = viewModel.amendContractRequestModel.value
            model?.contract = ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId
            model?.reason = selectedReason

            if (model?.reason.isNullOrEmpty()) {
                showError(requireContext(), getString(R.string.please_select_amend_reason))
                return
            }

            if (model?.reason.equals(getString(R.string.something_else)) && binding.etSomethingElseReason.text.trim()
                    .isEmpty()
            ) {
                showError(requireContext(), getString(R.string.please_enter_other_contract_amendment_reason))
                return
            }
            if (binding.etSomethingElseReason.text.trim().isNotBlank()) {
                model?.reason = binding.etSomethingElseReason.text.trim().toString()
            }

            var enteredAmount = binding.etPrice.text.toString().trim().replace("$", "")
            var amount = if (enteredAmount.isEmpty()) 0.0 else enteredAmount.toDouble()
            model?.amount = String.format("%.2f", amount).toDouble()
            viewModel.validateContractAmendData()
        }
    }
}
