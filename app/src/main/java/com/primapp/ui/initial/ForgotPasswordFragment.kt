package com.primapp.ui.initial

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.primapp.R
import com.primapp.constants.VerifyOTPRequestTypes
import com.primapp.databinding.FragmentForgotPasswordBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ForgotDataViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_forgot_password

    val viewModel by viewModels<ForgotDataViewModel> { viewModelFactory }

    private var userId: Int? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setObserver()
    }


    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.forgotPasswordLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        userId = it.data?.content?.userId
                        DialogUtils.showCloseDialog(requireActivity(),R.string.otp_sent_success){
                            openVerifyOTP(userId)
                        }
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })
    }


    private fun openVerifyOTP(userId: Int?) {
        if (userId == null) return

        val action =
            ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToVerifyOTPFragment(
                null,
                "$userId",
                VerifyOTPRequestTypes.FORGOT_PASSWORD,
                viewModel.forgotDataModel.value!!.email!!
            )
        findNavController().navigate(action)
    }

    fun forgotPassword() {
        if (viewModel.validate()) {
            viewModel.forgotPassword(viewModel.forgotDataModel.value!!)
        }
    }

    fun cancel(){
        findNavController().popBackStack()
    }
}