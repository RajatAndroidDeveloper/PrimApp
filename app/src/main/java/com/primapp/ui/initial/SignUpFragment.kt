package com.primapp.ui.initial

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentSignUpBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DateTimeUtils
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.util.*


class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    //val adapter by lazy { GenderListAdapter { item -> onItemClick(item) } }

    override fun getLayoutRes(): Int = R.layout.fragment_sign_up

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()

    }

    private fun setData() {
        binding.frag = this

        binding.tlUserName.setEndIconOnClickListener {
            showHelperDialog(getString(R.string.username_help_text))
        }

    }

    private fun setAdapter() {
        context?.apply {
            val items = resources.getStringArray(R.array.gender)
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)

            mAutoCompleteGender.setAdapter(adapter)
        }
    }

    fun login() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    private fun onItemClick(any: Any) {

    }

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        //open caleder 18 years from now
        cal.add(Calendar.YEAR, -18)

        val picker = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.datePicker.maxDate = cal.timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker.getDatePicker().getTouchables().get(1).performClick()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.getDatePicker().getTouchables().get(0).performClick()
        }

        picker.show()
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth

            binding.etDOB.setText(DateTimeUtils.getDateFromPicker(cal, DateTimeUtils.DOB_FORMAT))

/*
            val model = viewModel.updateProfileReqModel.value
            model?.dob = DateTimeUtils.getDateFromPicker(cal, DateTimeUtils.SEND_DATE_FORMAT)
            viewModel.updateProfileReqModel.value = model
*/
        }

}