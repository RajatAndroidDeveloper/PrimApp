package com.primapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.databinding.FragmentAccountSettingsBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.CommunitiesViewModel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.DisconnectHandler
import com.sendbird.android.handler.PushRequestCompleteHandler
import com.sendbird.android.push.SendbirdPushHelper
import kotlinx.android.synthetic.main.toolbar_inner_back.toolbar

class AccountSettingsFragment: BaseFragment<FragmentAccountSettingsBinding>() {
    val viewModel by viewModels<CommunitiesViewModel> { viewModelFactory }
    override fun getLayoutRes(): Int {
        return R.layout.fragment_account_settings
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.account_settings), toolbar)
        setObserver()
        binding.llDeleteAccount.setOnClickListener {
            deleteAccount()
        }
    }

    fun setObserver(){
        viewModel.deleteAccountLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.SUCCESS -> {
                        showLoading()
                        SendbirdPushHelper.unregisterPushHandler(object :
                            PushRequestCompleteHandler {
                            override fun onComplete(p0: Boolean, p1: String?) {
                                hideLoading()
                                logoutFromSendBird()
                            }

                            override fun onError(e: SendbirdException) {
                                hideLoading()
                                Log.d(ConnectionManager.TAG, "Failed to unregister push helper ${e?.code}")
                                logoutFromSendBird()
                            }
                        })
                    }
                }
            }
        })
    }

    fun logoutFromSendBird() {
        showLoading()
        ConnectionManager.logout(DisconnectHandler {
            hideLoading()
            val analyticsData = Bundle()
            analyticsData.putInt("user_id", UserCache.getUserId(requireActivity()))
            analyticsManager.logEvent(AnalyticsManager.EVENT_LOGOUT, analyticsData)
            UserCache.clearAll(requireActivity())
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        })
    }
    private fun deleteAccount() {
        var message = "${UserCache.getUser(requireContext())?.firstName+" "+UserCache.getUser(requireContext())?.lastName}, we're sorry to see you go.\n" +
                "Are you sure you want to delete account?\n" +
                "This means that you will lose your user-generated content that's shared with others, such as photos, videos, text posts and reviews.\n" +
                "Once deleted, you will not be able to reverse this deletion process."

        DialogUtils.showDeleteAccountDialog(requireActivity(), message) {
            when(it){
                "Delete"->{
                    DialogUtils.showYesNoDialog(requireActivity(), R.string.are_you_sure_you_want_to_delete_account, {
                        viewModel.deleteYourAccount(UserCache.getUserId(requireContext()))
                    })
                }
            }
        }
    }
}