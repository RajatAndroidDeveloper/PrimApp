package com.primapp.ui.settings.security

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentSecurityBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class SecurityFragment() : BaseFragment<FragmentSecurityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_security

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.security), toolbar)
    }
}