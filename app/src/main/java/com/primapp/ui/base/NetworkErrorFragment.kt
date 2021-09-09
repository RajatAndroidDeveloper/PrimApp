package com.primapp.ui.base

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentNetworkErrorBinding
import com.primapp.utils.AnalyticsManager

class NetworkErrorFragment : BaseFragment<FragmentNetworkErrorBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_network_error

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        analyticsManager.trackScreenView(AnalyticsManager.SCREEN_NETWORK_ERROR)
    }
}