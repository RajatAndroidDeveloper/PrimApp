package com.primapp.ui.base

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentNetworkErrorBinding

class NetworkErrorFragment : BaseFragment<FragmentNetworkErrorBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_network_error

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}