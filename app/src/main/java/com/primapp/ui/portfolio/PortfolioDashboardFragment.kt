package com.primapp.ui.portfolio

import android.os.Bundle
import com.primapp.R
import com.primapp.databinding.FragmentPortfolioDashboardBinding
import com.primapp.extensions.showInfo
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class PortfolioDashboardFragment : BaseFragment<FragmentPortfolioDashboardBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_portfolio_dashboard

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.portfolio), toolbar)
        setData()
    }

    fun setData(){
        binding.frag = this
    }

    fun onAddMentoringPortfolio(){
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }

    fun onAddExperiences(){
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }

    fun onAddSkills(){
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }

    fun onAddBenefits(){
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }
}