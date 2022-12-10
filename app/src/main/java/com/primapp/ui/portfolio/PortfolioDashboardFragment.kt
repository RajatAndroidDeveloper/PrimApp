package com.primapp.ui.portfolio

import android.app.DownloadManager
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentPortfolioDashboardBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.DownloadFile
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.MentoringPortfolioAdapter
import com.primapp.ui.portfolio.adapter.PortfolioBenefitsAdapter
import com.primapp.ui.portfolio.adapter.PortfolioExperienceAdapter
import com.primapp.ui.portfolio.adapter.PortfolioSkillsNCertificateAdapter
import com.primapp.utils.DownloadUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import javax.inject.Inject


class PortfolioDashboardFragment : BaseFragment<FragmentPortfolioDashboardBinding>() {

    var userId: Int? = null

    @Inject
    lateinit var downloadManager: DownloadManager

    private val adapterMentoringPortfolio by lazy { MentoringPortfolioAdapter { item -> onItemClick(item) } }
    private val adapterPortfolioExperience by lazy { PortfolioExperienceAdapter() }
    private val adapterPortfolioSkillsNCertificate by lazy { PortfolioSkillsNCertificateAdapter() }
    private val adapterPortfolioBenefits by lazy { PortfolioBenefitsAdapter() }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_portfolio_dashboard

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.portfolio), toolbar)
        setData()
        setAdapter()
        setObserver()
    }

    fun setData() {
        binding.frag = this
        userId = PortfolioDashboardFragmentArgs.fromBundle(requireArguments()).userId

        if (isLoaded) {
            return
        }
        viewModel.getPortfolioData(1)
    }

    private fun setObserver() {
        viewModel.userPortfolioLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.let { response ->
                            binding.portfolioData = response.content

                            response.content.mentoringPortfolio?.let { mentrPortfolio ->
                                adapterMentoringPortfolio.addData(mentrPortfolio)
                            }

                            response.content.experiences?.let { exp ->
                                adapterPortfolioExperience.addData(exp)
                            }

                            response.content.skills_certificate?.let { skillNCer ->
                                adapterPortfolioSkillsNCertificate.addData(skillNCer)
                            }

                            response.content.benefits?.let { benefitData ->
                                adapterPortfolioBenefits.addData(benefitData)
                            }
                        }
                    }
                }
            }
        })
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
        findNavController().navigate(R.id.addBenefitsFragment)
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }
            is ShowVideo -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.videoViewDialog, bundle)
            }
            is DownloadFile -> {
                DownloadUtils.download(requireContext(), downloadManager, item.url)
            }
        }
    }
}