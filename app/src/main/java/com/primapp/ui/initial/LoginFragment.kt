package com.primapp.ui.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentLoginBinding
import com.primapp.databinding.FragmentSignUpBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.viewmodels.LoginViewModel


class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    val viewModel by viewModels<LoginViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_login

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.frag = this
        binding.viewModel = viewModel
    }



    fun signUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}