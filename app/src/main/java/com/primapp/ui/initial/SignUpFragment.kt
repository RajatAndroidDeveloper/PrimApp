package com.primapp.ui.initial

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentSignUpBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DateTimeUtils
import com.primapp.viewmodels.SignUpViewModel
import java.util.*
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.primapp.constants.ReferenceEntityTypes
import com.primapp.constants.VerifyOTPRequestTypes
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.utils.DialogUtils


class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    val viewModel by viewModels<SignUpViewModel> { viewModelFactory }

    val genderAdapter by lazy {
        AutocompleteListArrayAdapter(
            requireContext(),
            R.layout.item_simple_text
        )
    }
    val countryAdapter by lazy {
        AutocompleteListArrayAdapter(
            requireContext(),
            R.layout.item_simple_text
        )
    }

    override fun getLayoutRes(): Int = R.layout.fragment_sign_up

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setAdapter()
        setObserver()

        if (isLoaded) {
            return
        }
        getReferenceData()
    }

    private fun setData() {
        binding.frag = this

        binding.viewModel = viewModel

        binding.tlUserName.setEndIconOnClickListener {
            DialogUtils.showCloseDialog(requireActivity(), R.string.username_help_text, R.drawable.question_mark)
        }
    }

    private fun setObserver() {
        viewModel.referenceLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.SUCCESS -> {
                    when (it.data?.content?.entity) {
                        ReferenceEntityTypes.GENDER_LIST -> {
                            it.data.content.referenceItemsList?.apply {
                                genderAdapter.addAll(this)
                            }
                        }
                        ReferenceEntityTypes.COUNTRY_LIST -> {
                            it.data.content.referenceItemsList?.apply {
                                val usaItem = this.find { it.itemValue.equals("US") }
                                usaItem?.let {
                                    val index: Int = this.indexOf(it)
                                    this.removeAt(index)
                                    this.add(0, it)
                                }
                                countryAdapter.addAll(this)
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    it.message?.apply {
                        showError(requireContext(), this)
                    }
                    findNavController().popBackStack()
                }
                Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.signUpLiveDataLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.otp_sent_success) {
                            val action =
                                SignUpFragmentDirections.actionSignUpFragmentToVerifyOTPFragment(
                                    viewModel.signUpRequestDataModel.value,
                                    null,
                                    VerifyOTPRequestTypes.SIGN_UP,
                                    viewModel.signUpRequestDataModel.value!!.email!!
                                )
                            findNavController().navigate(action)
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


    private fun getReferenceData() {
        viewModel.getReferenceData(ReferenceEntityTypes.GENDER_LIST)
        viewModel.getReferenceData(ReferenceEntityTypes.COUNTRY_LIST)
    }

    private fun setAdapter() {
        context?.apply {


            binding.mAutoCompleteGender.setAdapter(genderAdapter)

            binding.mAutoCompleteGender.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = genderAdapter.contains(p0.toString())
                    val data = viewModel.signUpRequestDataModel.value
                    if (isDataValid) {
                        data?.gender = genderAdapter.getItemId(p0.toString())
                    } else {
                        data?.gender = null
                    }

                    viewModel.signUpRequestDataModel.value = data

                    return isDataValid
                }
            }



            binding.mAutoCompleteCountry.setAdapter(countryAdapter)

            binding.mAutoCompleteCountry.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = countryAdapter.contains(p0.toString())
                    val data = viewModel.signUpRequestDataModel.value
                    if (isDataValid) {
                        data?.countryIsoCode = countryAdapter.getItemKey(p0.toString())
                    } else {
                        data?.countryIsoCode = null
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
        binding.mAutoCompleteGender.clearFocus()
        binding.mAutoCompleteCountry.clearFocus()
        if (viewModel.validateSignUpData() && !binding.chkPrivacyPolicy.isChecked) {
            DialogUtils.showCloseDialog(requireActivity(), R.string.privacy_policy_help_text, R.drawable.question_mark)
        } else if (viewModel.validateSignUpData()) {
            viewModel.signUpUser()
        }
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