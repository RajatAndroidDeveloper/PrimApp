package com.primapp.ui.initial

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.VerifyOTPRequestTypes
import com.primapp.databinding.FragmentVerifyOtpBinding
import com.primapp.extensions.showError
import com.primapp.model.auth.SignUpRequestDataModel
import com.primapp.model.auth.UserData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.VerifyOTPViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class VerifyOTPFragment : BaseFragment<FragmentVerifyOtpBinding>() {

    val viewModel by viewModels<VerifyOTPViewModel> { viewModelFactory }

    private lateinit var counter: CountDownTimer
    private var isCounterActive: Boolean = true
    private var signUpRequestModel: SignUpRequestDataModel? = null

    private var requestType: VerifyOTPRequestTypes = VerifyOTPRequestTypes.SIGN_UP
    private var userId: String? = null
    private var email: String? = null

    override fun getLayoutRes(): Int = R.layout.fragment_verify_otp

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        setData()
        setObservers()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
        VerifyOTPFragmentArgs.fromBundle(requireArguments()).let {
            signUpRequestModel = it.signupData
            requestType = it.requestType
            userId = it.userId
            email = it.email
        }
    }

    private fun setObservers() {
        viewModel.verifyOTPLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.LOADING -> {
                    showLoading()
                }
                Status.ERROR -> {
                    showError(requireContext(), it.message!!)
                }
                Status.SUCCESS -> {
                    if (it.data?.content == null) {
                        showError(requireContext(), getString(R.string.something_went_wrong))
                    } else {
                        saveUserDataToCache(it.data.content)
                        DialogUtils.showCloseDialog(requireActivity(), R.string.account_success) {
                            val action =
                                VerifyOTPFragmentDirections.actionVerifyOTPFragmentToCommunitiesFragment(true)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })

        viewModel.forgotUsernameVerifyOTP.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.LOADING -> {
                    showLoading()
                }
                Status.ERROR -> {
                    showError(requireContext(), it.message!!)
                }
                Status.SUCCESS -> {
                    DialogUtils.showCloseDialog(requireActivity(), R.string.forgot_username_verify_success) {
                        val action = VerifyOTPFragmentDirections.actionVerifyOTPFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        })

        viewModel.forgotPasswordVerifyOTP.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            val action =
                                VerifyOTPFragmentDirections.actionVerifyOTPFragmentToPasswordVerificationFragment(
                                    userId, PasswordVerificationFragment.FORGOT_PASSWORD, it.email, viewModel.verifyOTPRequestModel.value?.code
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })

        viewModel.resendOTPLiveData.observe(viewLifecycleOwner, Observer {
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
                        DialogUtils.showCloseDialog(requireActivity(), R.string.resend_otp_description)
                    }
                }
            }
        })

    }

    private fun saveUserDataToCache(data: UserData) {
        UserCache.saveAccessToken(requireContext(), data.token)
        //UserCache.saveFCMToken(context!!,data.fcmToken)
        UserCache.saveUser(requireContext(), data)
    }

    fun verifyUser() {
        if (viewModel.validateOTPData()) {
            when (requestType) {
                VerifyOTPRequestTypes.SIGN_UP -> {
                    signUpRequestModel?.code = viewModel.verifyOTPRequestModel.value?.code
                    signUpRequestModel?.let {
                        viewModel.verifyOTPForSignUp(it)
                    }
                }
                VerifyOTPRequestTypes.FORGOT_USERNAME -> {
                    if (userId == null) {
                        showError(requireContext(), getString(R.string.something_went_wrong))
                        findNavController().popBackStack()
                    } else {
                        viewModel.forgotUsernameVerifyOTP(userId!!)
                    }
                }

                VerifyOTPRequestTypes.FORGOT_PASSWORD -> {
                    if (userId == null) {
                        showError(requireContext(), getString(R.string.something_went_wrong))
                        findNavController().popBackStack()
                    } else {
                        viewModel.forgotPasswordVerifyOTP(userId!!)
                    }
                }
            }

        }
    }

//    private fun countDown() {
//        counter = object : CountDownTimer(30000, 1000) {
//
//            override fun onTick(millisUntilFinished: Long) {
//                isCounterActive = true
//                tvOTPCount.text =
//                    "0:${"%02d".format("${millisUntilFinished / 1000}".toInt())}"
//            }
//
//            override fun onFinish() {
//                isCounterActive = false
//                //Change color of TextView and make enable
//                setResendOTPView(isCounterActive)
//            }
//        }
//
//        counter.start()
//    }
//
//    fun setResendOTPView(isCounterActive: Boolean) {
//        tvResendOtp.isEnabled = !isCounterActive
//        if (isCounterActive) {
//            //Disabled View
//            binding..setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
//        } else {
//            //EnabledView
//            tvResendOtp.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
//        }
//    }

    override fun onDestroy() {
        //  counter.cancel()
        super.onDestroy()

    }

    fun resendCode() {
        viewModel.resendOTP(email!!)
    }

}