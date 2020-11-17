package com.primapp.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentSettingsBinding
import com.primapp.ui.MainActivity
import com.primapp.ui.base.BaseFragment
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
    }

    fun openAboutUs() {
        findNavController().navigate(R.id.action_settingsFragment_to_aboutUsFragment)
    }

    fun logout() {
        UserCache.clearAll(requireContext())
        startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()
    }
}