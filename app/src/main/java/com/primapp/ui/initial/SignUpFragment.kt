package com.primapp.ui.initial

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson
import com.primapp.R
import com.primapp.databinding.FragmentSignUpBinding
import com.primapp.model.auth.CountryListDataModel
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DateTimeUtils
import com.primapp.viewmodels.LoginViewModel
import com.primapp.viewmodels.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.util.*


class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    //val adapter by lazy { GenderListAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<SignUpViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_sign_up

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()

    }

    override fun onDialogDismiss(any: Any?) {
        super.onDialogDismiss(any)
        Log.d("anshul_dialog", "dismissed 2")
    }

    private fun setData() {
        binding.frag = this

        binding.viewModel = viewModel

        binding.tlUserName.setEndIconOnClickListener {
            showHelperDialog(getString(R.string.username_help_text), R.id.signUpFragment)
        }


    }

    private fun setAdapter() {
        context?.apply {
            val itemsGender: ArrayList<CountryListDataModel> = ArrayList<CountryListDataModel>()
            itemsGender.add(CountryListDataModel("m", "Male"))
            itemsGender.add(CountryListDataModel("f", "Female"))
            itemsGender.add(CountryListDataModel("o", "Others"))

            val adapter = CountryListArrayAdapter(this, R.layout.item_simple_text, itemsGender)

            mAutoCompleteGender.setAdapter(adapter)

            mAutoCompleteGender.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = adapter.contains(p0.toString())
                    val data = viewModel.signUpRequestDataModel.value
                    if (isDataValid) {
                        data?.gender = adapter.getItemKey(p0.toString())
                    } else {
                        data?.gender = null
                    }

                    viewModel.signUpRequestDataModel.value = data

                    return isDataValid
                }
            }
        }
    }

    fun login() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun registerNow() {
        mAutoCompleteGender.clearFocus()
        mAutoCompleteCountry.clearFocus()
        if (viewModel.signUpUser() && !chkPrivacyPolicy.isChecked)
            showHelperDialog(getString(R.string.privacy_policy_help_text))

        //findNavController().navigate(R.id.action_signUpFragment_to_verifyOTPFragment)
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
            picker.datePicker.touchables[1].performClick()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.datePicker.touchables[0].performClick()
        }

        picker.show()
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth

            binding.etDOB.setText(DateTimeUtils.getDateFromPicker(cal))

            val model = viewModel.signUpRequestDataModel.value
            model?.dateOfBirth = cal.timeInMillis.toString()
            viewModel.signUpRequestDataModel.value = model
        }

}