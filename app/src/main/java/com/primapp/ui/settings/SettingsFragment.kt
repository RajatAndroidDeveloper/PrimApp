package com.primapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.primapp.BuildConfig
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.databinding.FragmentSettingsBinding
import com.primapp.retrofit.ApiConstant
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.profile.UserPostsFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.DialogUtils
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.SendBirdPushHelper
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_settings

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.settings), toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_SETTINS)
        binding.tvAppVersion.text = "v${BuildConfig.VERSION_NAME}"
    }

    fun openAboutUs() {
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.about_us))
        bundle.putString("url", ApiConstant.ABOUT_US)
        findNavController().navigate(R.id.commonWebView, bundle)
    }

    fun openHelpSupport() {
        findNavController().navigate(R.id.helpAndSupportFragment)
    }

    fun openSecurity() {
        findNavController().navigate(R.id.securityFragment)
    }

    fun openBookmarks() {
        val bundle = Bundle()
        bundle.putString("type", UserPostsFragment.BOOKMARK_POST)
        bundle.putInt("userId", UserCache.getUserId(requireContext()))
        findNavController().navigate(R.id.userPostsFragment, bundle)
    }

    fun openRewards() {
        val bundle = Bundle()
        bundle.putString("title", "Prim Rewards")
        bundle.putString("url", ApiConstant.PRIM_REWARDS)
        findNavController().navigate(R.id.commonWebView, bundle)
    }

    fun logout() {
        DialogUtils.showYesNoDialog(requireActivity(), R.string.logout_message, {
            showLoading()
            SendBirdPushHelper.unregisterPushHandler(object : SendBirdPushHelper.OnPushRequestCompleteListener {
                override fun onComplete(p0: Boolean, p1: String?) {
                    hideLoading()
                    logoutFromSendBird()
                }

                override fun onError(p0: SendBirdException?) {
                    hideLoading()
                    Log.d(ConnectionManager.TAG, "Failed to unregister push helper ${p0?.code}")
                    logoutFromSendBird()
                }
            })


        })
    }

    fun logoutFromSendBird() {
        showLoading()
        ConnectionManager.logout(SendBird.DisconnectHandler {
            hideLoading()
            val analyticsData = Bundle()
            analyticsData.putInt("user_id", UserCache.getUserId(requireContext()))
            analyticsManager.logEvent(AnalyticsManager.EVENT_LOGOUT, analyticsData)
            UserCache.clearAll(requireContext())
            startActivity(Intent(requireContext(), MainActivity::class.java))
            activity?.finish()
        })
    }
}