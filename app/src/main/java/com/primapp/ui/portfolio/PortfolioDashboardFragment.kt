package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentPortfolioDashboardBinding
import com.primapp.extensions.showInfo
import com.primapp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class PortfolioDashboardFragment : BaseFragment<FragmentPortfolioDashboardBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_portfolio_dashboard

    private val adapterMentoringPortfolio by lazy { MentoringPortfolioAdapter() }
    private val adapterPortfolioExperience by lazy { PortfolioExperienceAdapter() }
    private val adapterPortfolioSkillsNCertificate by lazy { PortfolioSkillsNCertificateAdapter() }
    private val adapterPortfolioBenefits by lazy { PortfolioBenefitsAdapter() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.portfolio), toolbar)
        setData()
        setAdapter()
    }

    fun setData() {
        binding.frag = this
    }

    private fun setAdapter() {
        binding.rvMentoringPortfolio.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvMentoringPortfolio.adapter = adapterMentoringPortfolio

        binding.rvExperience.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvExperience.adapter = adapterPortfolioExperience

        binding.rvSkillsCertificate.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvSkillsCertificate.adapter = adapterPortfolioSkillsNCertificate

        binding.rvBenefits.apply {
            this.layoutManager = FlexboxLayoutManager(requireContext())
        }
        binding.rvBenefits.adapter = adapterPortfolioBenefits
    }

    fun onAddMentoringPortfolio() {
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }

    fun onAddExperiences() {
        findNavController().navigate(R.id.addExperienceFragment)
    }

    fun onAddSkills() {
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }

    fun onAddBenefits() {
        showInfo(requireContext(), getString(R.string.not_yet_implemented))
    }
}