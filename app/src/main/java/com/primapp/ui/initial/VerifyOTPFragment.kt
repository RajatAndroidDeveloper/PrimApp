package com.primapp.ui.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.primapp.R
import com.primapp.databinding.FragmentVerifyOtpBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class VerifyOTPFragment : BaseFragment<FragmentVerifyOtpBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_verify_otp

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
    }

    fun resendCode() {
        showHelperDialog(getString(R.string.resend_otp_description))
    }

}