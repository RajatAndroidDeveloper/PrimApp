package com.primapp.ui.contract

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.primapp.R
import com.primapp.databinding.FragmentCreateContractBinding
import com.primapp.extensions.showError
import com.primapp.model.contract.ResultsItem
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.util.*
import kotlin.contracts.contract


class CreateContractFragment : BaseFragment<FragmentCreateContractBinding>() {

    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    private var selectedDateType: String = ""
    override fun getLayoutRes(): Int = R.layout.fragment_create_contract
    private var contractData: ResultsItem? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
        setToolbar(getString(R.string.create_contracts), toolbar)
        attachObservers()
        editTextFocusListener()

        if (CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details") {
            binding.from = "contract_details"
            contractData = Gson().fromJson(CreateContractFragmentArgs.fromBundle(requireArguments()).contractData, ResultsItem::class.java)
            setUpContractData(contractData)
        }
    }

    private fun setUpContractData(contractData: ResultsItem?) {
        var model = viewModel.updateContractRequestModel.value
        model?.price = (contractData?.price ?: "0.0").toDouble()
        model?.name = contractData?.name ?: ""
        model?.scopeOfWork = contractData?.scopeOfWork ?: ""
        model?.startDate = contractData?.startDate.toString()
        model?.endDate = contractData?.endDate.toString()
        model?.contractStatus = contractData?.contractStatus

        binding.etStartDate.setText(DateTimeUtils.getDateFromMillisValue(contractData?.startDate))
        binding.etEndDate.setText(DateTimeUtils.getDateFromMillisValue(contractData?.endDate))
        binding.etPrice.setText("$" + contractData?.price)
    }

    fun openDatePicker(dateType: String) {
        selectedDateType = dateType
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

        val picker = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        val model = viewModel.createContractRequestModel.value
        val updateModel = viewModel.createContractRequestModel.value
        if (!model?.startDate.isNullOrEmpty() && dateType == END_DATE) {
            if(CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details"){
                picker.datePicker.minDate = updateModel?.startDate!!.toLong()
            } else {
                picker.datePicker.minDate = model?.startDate!!.toLong()
            }
        } else if (!model?.endDate.isNullOrEmpty() && dateType == START_DATE) {
            if(CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details"){
                picker.datePicker.minDate = updateModel?.endDate!!.toLong()
            } else {
                picker.datePicker.minDate = model?.endDate!!.toLong()
            }
        } else {
            picker.datePicker.minDate = cal.timeInMillis
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker.datePicker.touchables[1].performClick()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.datePicker.touchables[0].performClick()
        }

        picker.show()
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month
        cal[Calendar.DAY_OF_MONTH] = dayOfMonth

        val model = viewModel.createContractRequestModel.value
        val updateModel = viewModel.updateContractRequestModel.value

        when (selectedDateType) {
            START_DATE -> {
                binding.etStartDate.setText(DateTimeUtils.getDateFromPicker(cal))
                if(CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details"){
                    updateModel?.startDate = cal.timeInMillis.toString()
                }else {
                    model?.startDate = cal.timeInMillis.toString()
                }
            }
            END_DATE -> {
                binding.etEndDate.setText(DateTimeUtils.getDateFromPicker(cal))
                if(CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details"){
                    updateModel?.endDate = cal.timeInMillis.toString()
                }else {
                    model?.endDate = cal.timeInMillis.toString()
                }
            }
        }
    }

    private fun attachObservers() {
        viewModel.createContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_created_successfully, R.drawable.correct
                        ) {
                            findNavController().navigate(R.id.action_createContractFragment2_to_currentProjectsFragment)
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

        viewModel.updateContractDetailLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_updated_successfully, R.drawable.correct
                        ) {
                            findNavController().navigate(R.id.action_createContractFragment2_to_currentProjectsFragment)
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

    fun createContract() {
        if (CreateContractFragmentArgs.fromBundle(requireArguments()).from == "contract_details") {
            val model = viewModel.updateContractRequestModel.value
            model?.name = binding.etContractName.text.toString().trim()
            model?.scopeOfWork = binding.etScopeOfWork.text.toString().trim()
            var amountValue = binding.etPrice.text.toString().trim().replace("$","")
            var amount = if (amountValue.isEmpty()) 0.0 else amountValue.toDouble()
            model?.price = String.format("%.2f", amount).toDouble()

            viewModel.validateUpdateContractData(contractData?.id?:0)
        } else {
            val model = viewModel.createContractRequestModel.value
            model?.name = binding.etContractName.text.toString().trim()
            model?.scopeOfProject = binding.etScopeOfWork.text.toString().trim()
            var amountValue = binding.etPrice.text.toString().trim().replace("$","")
            var amount = if (amountValue.isEmpty()) 0.0 else amountValue.toDouble()
            model?.price = String.format("%.2f", amount).toDouble()

            viewModel.validateData()
        }
    }

    private fun editTextFocusListener(){
        if(CreateContractFragmentArgs.fromBundle(requireArguments()).from != "contract_details")
        binding.etPrice.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etPrice.setText("$")
            }
        }
    }

    companion object {
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }
}