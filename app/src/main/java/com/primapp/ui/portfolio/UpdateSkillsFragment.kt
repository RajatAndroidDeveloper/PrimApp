package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentUpdateSkillsBinding
import com.primapp.extensions.showError
import com.primapp.model.DeleteItem
import com.primapp.model.auth.ReferenceItems
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.UpdateSkillsAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_community_back.*

class UpdateSkillsFragment : BaseFragment<FragmentUpdateSkillsBinding>() {

    lateinit var portfolioContent: PortfolioContent

    var skillsList = ArrayList<ReferenceItems>()

    private val adapter by lazy { UpdateSkillsAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_update_skills

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.skills_and_certifications), toolbar)
        setAdapter()
        setData()
        setObserver()
    }

    private fun setAdapter() {
        binding.rvSkills.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvSkills.adapter = adapter
    }

    fun setData() {
        binding.frag = this
        portfolioContent = AddBenefitsFragmentArgs.fromBundle(requireArguments()).portfolioData

        portfolioContent.skills_certificate?.let {
            adapter.addData(it)
        }

        ivAdd.setOnClickListener {
            DialogUtils.showSearchTextDialog(
                requireActivity(),
                getString(R.string.skills_and_certifications),
                skillsList,
                {
                    it?.let {
                        viewModel.addSkill(it)
                    }
                }
            )
        }
        viewModel.getSkillsList()
    }

    private fun setObserver() {
        viewModel.skillsListLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            skillsList = it.map {
                                ReferenceItems(it.id, it.name.toString(), it.name.toString())
                            } as ArrayList<ReferenceItems>
                        }
                    }
                }
            }
        })

        viewModel.addSkillLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.let {
                            adapter.addItem(it.content)
                            //Update list to avoid api call
                            portfolioContent.skills_certificate = adapter.list
                        }
                    }
                }
            }
        })

        viewModel.deleteSkillsLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            adapter.deleteItem(it.id)
                            //Update list to avoid api call
                            portfolioContent.skills_certificate = adapter.list
                        }
                    }
                }
            }
        })
    }


    private fun onItemClick(item: Any?) {
        when (item) {

            is DeleteItem -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_skills_msg, {
                    viewModel.deleteSkillFromPortfolio(item.id)
                })
            }
        }
    }
}