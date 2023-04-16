package com.primapp.ui.contract

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentCreateContractBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.util.*


class CreateContractFragment : BaseFragment<FragmentCreateContractBinding>() {

    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    private var selectedDateType: String = ""
    override fun getLayoutRes(): Int = R.layout.fragment_create_contract

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
        setToolbar(getString(R.string.create_contracts), toolbar)
        attachObservers()
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
        if (!model?.startDate.isNullOrEmpty() && dateType == END_DATE) {
            picker.datePicker.minDate = model?.startDate!!.toLong()
        } else if (!model?.endDate.isNullOrEmpty() && dateType == START_DATE) {
            picker.datePicker.minDate = model?.endDate!!.toLong()
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

        when (selectedDateType) {
            START_DATE -> {
                binding.etStartDate.setText(DateTimeUtils.getDateFromPicker(cal))
                model?.startDate = cal.timeInMillis.toString()
            }
            END_DATE -> {
                binding.etEndDate.setText(DateTimeUtils.getDateFromPicker(cal))
                model?.endDate = cal.timeInMillis.toString()
            }
        }

        viewModel.createContractRequestModel.value = model
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

    fun createContract() {
        val model = viewModel.createContractRequestModel.value
        var amount = if (binding.etPrice.text.toString().isEmpty()) 0.0 else binding.etPrice.text?.trim().toString().toDouble()
        model?.price =  String.format("%.2f", amount).toDouble()
        viewModel.validateData()
    }

    companion object {
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }
}