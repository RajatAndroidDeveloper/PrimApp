package com.primapp.ui.settings.help

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentHelpAndSupportBinding
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class HelpAndSupportFragment() : BaseFragment<FragmentHelpAndSupportBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_help_and_support

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.help_n_support), toolbar)
        setData()

    }

    private fun setData() {
        binding.frag = this
    }

    fun openReportProblem() {
        findNavController().navigate(R.id.reportProblemFragment)
    }
}