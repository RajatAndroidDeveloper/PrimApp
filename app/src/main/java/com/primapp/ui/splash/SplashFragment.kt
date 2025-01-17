package com.primapp.ui.splash

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentSplashBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.NetworkConnectionHelper
import com.primapp.utils.checkIsNetworkConnected
import kotlinx.coroutines.*

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_splash

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_SPLASH_VIEW)

        setObserver()

        if (UserCache.isLoggedIn(requireContext())) {
            if (checkIsNetworkConnected(requireContext()))
                viewModel.getUserData(UserCache.getUser(requireContext())!!.id)
            else
                findNavController().navigate(R.id.networkErrorFragment)
        } else {
            activityScope.launch {
                delay(2000)
                gotoLogin()
            }
        }
    }

    private fun setObserver() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.content?.let { it1 ->
                            UserCache.saveUser(requireContext(), it1)
                            analyticsManager.logUserProperties(it1)
                            gotoNextActivity()
                        }
                    }
                    Status.ERROR -> {
                        if (it.errorCode == 401) {
                            showError(requireContext(), "Session expired")
                            UserCache.clearAll(requireContext())
                            gotoLogin()
                        } else {
                            showError(requireContext(), it.message!!)
                            findNavController().navigate(R.id.action_splashFragment_to_networkErrorFragment)
                        }
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun gotoNextActivity() {
        findNavController().navigate(R.id.dashboardActivity)
        activity?.finish()
    }

    fun gotoLogin() {
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}