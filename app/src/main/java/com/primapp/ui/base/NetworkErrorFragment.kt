package com.primapp.ui.base

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentNetworkErrorBinding
import com.primapp.ui.splash.SplashFragmentDirections
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.NetworkConnectionHelper
import com.primapp.utils.checkIsNetworkConnected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkErrorFragment : BaseFragment<FragmentNetworkErrorBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_network_error

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_NETWORK_ERROR)

        binding.tvRetry.setOnClickListener {
            if (checkIsNetworkConnected(requireContext())){
                findNavController().popBackStack()
            }
        }
    }
}