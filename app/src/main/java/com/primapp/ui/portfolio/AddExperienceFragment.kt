package com.primapp.ui.portfolio

import android.app.DatePickerDialog
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
import com.primapp.model.portfolio.ExperienceData
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.util.*

class AddExperienceFragment : BaseFragment<FragmentAddExperienceBinding>() {

    private var portfolioContent: PortfolioContent? = null
    var experienceData: ExperienceData? = null
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
        portfolioContent = AddExperienceFragmentArgs.fromBundle(requireArguments()).portfolioData
        experienceData = AddExperienceFragmentArgs.fromBundle(requireArguments()).experienceData

        experienceData?.let {
            val data = viewModel.addExperienceRequestModel.value
            data?.title = it.title
            data?.jobType = it.jobType?.id
            data?.companyName = it.companyName
            data?.location = it.location
            data?.isCurrentCompany = it.isCurrentCompany
//            data?.years = it.years
//            data?.months = it.months
            data?.startDate = it.startDate
            data?.endDate = it.endDate

            binding.mAutoCompleteJobType.setText(it.jobType?.name)

//            binding.mAutoCompleteYears.setText(resources.getQuantityString(R.plurals.count_years, it.years, it.years))
//            binding.mAutoCompleteMonths.setText(
//                resources.getQuantityString(R.plurals.count_months, it.months, it.months)
//            )
            binding.chkCurrentCompany.isChecked = it.isCurrentCompany

            viewModel.addExperienceRequestModel.value = data
        }
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
                        it.data?.content?.let {
                            //Update list to avoid api call
                            portfolioContent?.experiences?.add(it)
                        }
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

        viewModel.updateExperienceLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        //Update list to avoid api call
                        it.data?.content?.let { data ->
                            val dataInList = portfolioContent?.experiences?.find { it.id == data.id }
                            val index = portfolioContent?.experiences?.indexOf(dataInList)
                            if (index != null && index != -1 && portfolioContent != null)
                                portfolioContent?.experiences?.set(index, data)
                        }
                        DialogUtils.showCloseDialog(requireActivity(), R.string.portfolio_experience_updated) {
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

            /*binding.mAutoCompleteYears.setAdapter(adapterYears)

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
            }*/

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
        //binding.mAutoCompleteYears.clearFocus()
        //binding.mAutoCompleteMonths.clearFocus()
        if (viewModel.validateData()) {
            if (experienceData != null) {
                viewModel.updateExperience(experienceData!!.id)
            } else {
                viewModel.addPortfolioExperience()
            }
        }
    }

    //Date Pickers

    fun openStartDatePicker() {
        val cal = Calendar.getInstance()

        val picker = DatePickerDialog(
            requireContext(),
            startDateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        picker.datePicker.maxDate = cal.timeInMillis

        picker.show()
    }

    private val startDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth

            val model = viewModel.addExperienceRequestModel.value
            model?.startDate = cal.timeInMillis
            model?.endDate = null
            viewModel.addExperienceRequestModel.value = model
        }

    fun openEndDatePicker() {
        val cal = Calendar.getInstance()

        val picker = DatePickerDialog(
            requireContext(),
            endDateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        viewModel.addExperienceRequestModel.value?.startDate?.let {
            picker.datePicker.minDate = it
        }
        picker.datePicker.maxDate = cal.timeInMillis

        picker.show()
    }

    private val endDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth

            val model = viewModel.addExperienceRequestModel.value
            model?.endDate = cal.timeInMillis
            viewModel.addExperienceRequestModel.value = model
        }
}