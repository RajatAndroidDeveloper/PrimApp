package com.primapp.ui.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentForgotUsernameBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class ForgotUsernameFragment : BaseFragment<FragmentForgotUsernameBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_forgot_username

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
    }

    fun cancel(){
        findNavController().popBackStack()
    }
}