package com.primapp.ui.portfolio

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.ReferenceEntityTypes
import com.primapp.databinding.FragmentAddExperienceBinding
import com.primapp.extensions.showError
import com.primapp.model.auth.ReferenceItems
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*

class AddExperienceFragment : BaseFragment<FragmentAddExperienceBinding>() {

    val adapterJobType by lazy { AutocompleteListArrayAdapter(requireContext(), R.layout.item_simple_text) }
    val adapterYears by lazy { AutocompleteListArrayAdapter(requireContext(), R.layout.item_simple_text) }
    val adapterMonths by lazy { AutocompleteListArrayAdapter(requireContext(), R.layout.item_simple_text) }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_add_experience

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.experiences), toolbar)
        setData()
        setAdapter()
        setObserver()
        setListeners()
        getReferenceData()
    }

    fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.referenceLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.content?.referenceItemsList?.apply {
                        adapterJobType.addAll(this)
                    }
                }
                Status.ERROR -> {
                    it.message?.apply {
                        showError(requireContext(), this)
                    }
                    findNavController().popBackStack()
                }
                Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.addPortfolioExperienceLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.portfolio_experience_added) {
                            findNavController().popBackStack()
                        }
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })
    }

    private fun getReferenceData() {
        viewModel.getReferenceData(ReferenceEntityTypes.JOBTYPE_LIST)
        val listOfYears = List(21) {
            ReferenceItems(
                it,
                resources.getQuantityString(R.plurals.count_years, it, it),
                resources.getQuantityString(R.plurals.count_years, it, it)
            )
        }
        val listOfMonths = List(12) {
            ReferenceItems(
                it,
                resources.getQuantityString(R.plurals.count_months, it, it),
                resources.getQuantityString(R.plurals.count_months, it, it)
            )
        }

        adapterYears.addAll(listOfYears)
        adapterMonths.addAll(listOfMonths)
    }

    private fun setAdapter() {
        context?.apply {
            binding.mAutoCompleteJobType.setAdapter(adapterJobType)

            binding.mAutoCompleteJobType.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = adapterJobType.contains(p0.toString())
                    val data = viewModel.addExperienceRequestModel.value
                    if (isDataValid) {
                        data?.jobType = adapterJobType.getItemId(p0.toString())
                    } else {
                        data?.jobType = null
                    }
                    viewModel.addExperienceRequestModel.value = data

                    return isDataValid
                }
            }

            binding.mAutoCompleteYears.setAdapter(adapterYears)

            binding.mAutoCompleteYears.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = adapterYears.contains(p0.toString())
                    val data = viewModel.addExperienceRequestModel.value
                    if (isDataValid) {
                        data?.years = adapterYears.getItemId(p0.toString())
                    } else {
                        data?.years = null
                    }
                    viewModel.addExperienceRequestModel.value = data

                    return isDataValid
                }
            }

            binding.mAutoCompleteMonths.setAdapter(adapterMonths)

            binding.mAutoCompleteMonths.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = adapterMonths.contains(p0.toString())
                    val data = viewModel.addExperienceRequestModel.value
                    if (isDataValid) {
                        data?.months = adapterMonths.getItemId(p0.toString())
                    } else {
                        data?.months = null
                    }
                    viewModel.addExperienceRequestModel.value = data

                    return isDataValid
                }
            }

        }
    }

    private fun setListeners() {
        binding.chkCurrentCompany.setOnCheckedChangeListener { compoundButton, b ->
            val data = viewModel.addExperienceRequestModel.value
            data?.isCurrentCompany = b
            viewModel.addExperienceRequestModel.value = data
        }
    }

    fun save() {
        binding.mAutoCompleteJobType.clearFocus()
        binding.mAutoCompleteYears.clearFocus()
        binding.mAutoCompleteMonths.clearFocus()
        if (viewModel.validateData()) {
            viewModel.addPortfolioExperience()
        }
    }
}