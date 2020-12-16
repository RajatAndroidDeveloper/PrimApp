package com.primapp.ui.initial

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentPasswordVerificationBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PasswordVerificationViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class PasswordVerificationFragment : BaseFragment<FragmentPasswordVerificationBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_password_verification

    val viewModel by viewModels<PasswordVerificationViewModel> { viewModelFactory }

    private var userId: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
        userId = PasswordVerificationFragmentArgs.fromBundle(requireArguments()).userId
    }

    private fun setObserver() {
        viewModel.changePasswordLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.password_reset_success) {
                            val action =
                                PasswordVerificationFragmentDirections.actionPasswordVerificationFragmentToLoginFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })
    }

    fun changePassword() {
        if (viewModel.validatePasswords()) {
            viewModel.changePassword(userId!!)
        }
    }
}