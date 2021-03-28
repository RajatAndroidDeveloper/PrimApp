package com.primapp.ui.initial

import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentPasswordVerificationBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PasswordVerificationViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class PasswordVerificationFragment : BaseFragment<FragmentPasswordVerificationBinding>() {

    private lateinit var type: String
    var email: String? = null
    var code: String? = null

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
        type = PasswordVerificationFragmentArgs.fromBundle(requireArguments()).type
        email = PasswordVerificationFragmentArgs.fromBundle(requireArguments()).email
        code = PasswordVerificationFragmentArgs.fromBundle(requireArguments()).code

        if (type == CHANGE_PASSWORD) {
            binding.tvHeading.isVisible = false
            binding.tlOldPassword.isVisible = true
            tvTitle.setText(getString(R.string.change_password))
        } else {
            //Forgot password
            binding.tlOldPassword.isVisible = false
            val data = viewModel.passwordVerificationRequestModel.value
            data?.email = email
            data?.code = code
            viewModel.passwordVerificationRequestModel.value = data
        }
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
                        DialogUtils.showCloseDialog(requireActivity(), R.string.password_change_success) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.resetPasswordLiveData.observe(viewLifecycleOwner, Observer {
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
        if (viewModel.validatePasswords(type)) {
            if (type == CHANGE_PASSWORD)
                viewModel.changePassword(userId!!)
            else
                viewModel.resetPassword(userId!!)
        }
    }

    companion object {
        const val FORGOT_PASSWORD = "forgotPassword"
        const val CHANGE_PASSWORD = "changePassword"
    }
}