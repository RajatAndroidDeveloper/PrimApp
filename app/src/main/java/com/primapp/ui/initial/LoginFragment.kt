package com.primapp.ui.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentLoginBinding
import com.primapp.databinding.FragmentSignUpBinding
import com.primapp.extensions.showError
import com.primapp.model.auth.UserData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.viewmodels.LoginViewModel


class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    val viewModel by viewModels<LoginViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_login

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.loginUserLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.ERROR -> {
                    showError(requireContext(), it.message!!)
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    if (it.data?.content == null) {
                        showError(requireContext(), getString(R.string.something_went_wrong))
                    } else {
                        openDashboardActivity(it.data.content)
                    }
                }

            }
        })
    }

    private fun openDashboardActivity(data: UserData) {
        UserCache.saveAccessToken(requireContext(), data.token)
        //UserCache.saveFCMToken(context!!,data.fcmToken)
        UserCache.saveUser(requireContext(), data)
        findNavController().navigate(R.id.dashboardActivity)
        activity?.finish()
    }

    fun signUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}