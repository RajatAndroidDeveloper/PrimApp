package com.primapp.ui.initial

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.primapp.R
import com.primapp.constants.VerifyOTPRequestTypes
import com.primapp.databinding.FragmentForgotUsernameBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.viewmodels.ForgotDataViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class ForgotUsernameFragment : BaseFragment<FragmentForgotUsernameBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_forgot_username

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
        viewModel.forgotUsernameLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        userId = it.data?.content?.userId
                        showCustomDialog(
                            getString(R.string.forgot_username_success),
                            R.id.forgotUsernameFragment
                        )
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

    override fun onDialogDismiss(any: Any?) {
        super.onDialogDismiss(any)
        Log.d("anshul_dialog", "dismissed 2 : ${Gson().toJson(any)}")
        openVerifyOTP(userId)
    }


    private fun openVerifyOTP(userId: Int?) {
        if (userId == null) return

        val action =
            ForgotUsernameFragmentDirections.actionForgotUsernameFragmentToVerifyOTPFragment(
                null,
                "$userId",
                VerifyOTPRequestTypes.FORGOT_USERNAME,
                viewModel.forgotDataModel.value!!.email!!
            )
        findNavController().navigate(action)
    }

    fun forgotUsername() {
        if (viewModel.validate()) {
            viewModel.forgotUsername(viewModel.forgotDataModel.value!!)
        }
    }

    fun cancel() {
        findNavController().popBackStack()
    }
}