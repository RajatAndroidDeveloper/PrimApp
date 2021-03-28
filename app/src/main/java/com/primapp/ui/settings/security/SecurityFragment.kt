package com.primapp.ui.settings.security

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentSecurityBinding
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.PasswordVerificationFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class SecurityFragment() : BaseFragment<FragmentSecurityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_security

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.security), toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
    }

    fun openChangePassword() {
        val bundle = Bundle()
        bundle.putString("userId", UserCache.getUserId(requireContext()).toString())
        bundle.putString("type", PasswordVerificationFragment.CHANGE_PASSWORD)
        findNavController().navigate(R.id.passwordVerificationFragment, bundle)
    }
}