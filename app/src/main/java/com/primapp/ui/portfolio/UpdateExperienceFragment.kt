package com.primapp.ui.portfolio

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.primapp.R
import com.primapp.databinding.FragmentUpdateExperienceBinding
import com.primapp.extensions.showError
import com.primapp.model.DeleteItem
import com.primapp.model.EditExperience
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.UpdateExperienceAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_community_back.*

class UpdateExperienceFragment : BaseFragment<FragmentUpdateExperienceBinding>() {

    private var portfolioContent: PortfolioContent? = null

    private val adapter by lazy { UpdateExperienceAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_update_experience

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.edit_experience), toolbar)
        setAdapter()
        setData()
        setObserver()
    }

    fun setData() {
        binding.frag = this

        ivAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("portfolioData", portfolioContent)
            findNavController().navigate(R.id.addExperienceFragment, bundle)
        }
        if (isLoaded || portfolioContent != null) {
            refreshData()
            return
        }
        portfolioContent = UpdateExperienceFragmentArgs.fromBundle(requireArguments()).portfolioData

        refreshData()
    }

    private fun refreshData() {
        portfolioContent?.experiences?.let {
            adapter.addData(it)
        }
    }

    private fun setAdapter() {
        binding.rvUpdateExperience.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rvUpdateExperience.adapter = adapter
    }

    private fun setObserver() {
        viewModel.deleteExperienceLiveData.observe(viewLifecycleOwner, Observer {
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
                            portfolioContent?.experiences = adapter.list
                        }
                    }
                }
            }
        })
    }


    private fun onItemClick(item: Any?) {
        when (item) {
            is EditExperience -> {
                val bundle = Bundle()
                bundle.putSerializable("experienceData", item.experienceData)
                bundle.putSerializable("portfolioData", portfolioContent)
                findNavController().navigate(R.id.addExperienceFragment, bundle)
            }

            is DeleteItem -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_experience_msg, {
                    viewModel.deleteExperience(item.id)
                })
            }
        }
    }
}