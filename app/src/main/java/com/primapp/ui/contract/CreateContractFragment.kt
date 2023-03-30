package com.primapp.ui.contract

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.AutoCompleteTextView
import com.primapp.R
import com.primapp.databinding.FragmentCreateContractBinding
import com.primapp.model.auth.ReferenceItems
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.utils.DateTimeUtils
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.util.*

class CreateContractFragment : BaseFragment<FragmentCreateContractBinding>() {

    private var selectedDateType: String = ""
    override fun getLayoutRes(): Int = R.layout.fragment_create_contract

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        setToolbar(getString(R.string.create_contracts), toolbar)
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
        picker.datePicker.maxDate = cal.timeInMillis

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

        when(selectedDateType) {
            START_DATE -> binding.etStartDate.setText(DateTimeUtils.getDateFromPicker(cal))
            END_DATE -> binding.etEndDate.setText(DateTimeUtils.getDateFromPicker(cal))
        }
    }

    companion object{
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }
}