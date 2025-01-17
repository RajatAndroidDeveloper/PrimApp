package com.primapp.ui.contract

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentProjectDetailsBinding
import com.primapp.extensions.setDivider
import com.primapp.extensions.showError
import com.primapp.model.contract.*
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.contract.adapters.AcceptRequestAdapter
import com.primapp.ui.contract.adapters.AmendRequestAdapter
import com.primapp.ui.contract.adapters.OnButtonClickCallback
import com.primapp.utils.DialogUtils
import com.primapp.utils.visible
import com.primapp.viewmodels.ContractsViewModel
import kotlinx.android.synthetic.main.fragment_project_details.*
import kotlinx.android.synthetic.main.toolbar_menu_more.*

class ProjectDetailsFragment : BaseFragment<FragmentProjectDetailsBinding>(), OnButtonClickCallback {

    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }
    override fun getLayoutRes() = R.layout.fragment_project_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar("", toolbar)
        binding.frag = this
        binding.user = UserCache.getUser(requireContext())
        binding.tvAboutDescription.setTrimLength(60)

        attachObservers()
        viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)

        ivEditData.setOnClickListener {
            var action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToCreateContractFragment2(
                "contract_details",
                Gson().toJson(binding.data)
            )
            findNavController().navigate(action)
        }

        binding.llRatingData.ratingBar.setOnRatingChangeListener { _, rating, fromUser ->
            Log.e("ratingBar", "rating:$rating -- fromUser: $fromUser")
            selectedRating = rating.toDouble()
        }
    }

    private fun attachObservers() {
        viewModel.contractDetailLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.data = it.data?.content
                        if (it.data?.content?.createdBy?.id == UserCache.getUserId(requireContext()) && it.data?.content?.contractStatus == "NOT_STARTED") ivEditData.visible(
                            true
                        )
                        it.data?.content?.let { it1 -> setUpDetailsPageData(it1) }
                        if (!it.data?.content?.amendRequest.isNullOrEmpty()) {
                            setUpAmendRequestAdapter(it.data?.content?.amendRequest as ArrayList<AmendRequestItem>)
                        }
                        if (!it.data?.content?.acceptedBy.isNullOrEmpty()) {
                            setUpAcceptRequestAdapter(it.data?.content?.acceptedBy as ArrayList<AcceptedByItem>)
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                            findNavController().popBackStack()
                        }
                    }
                    Status.LOADING -> {
                        binding.swipeRefresh.isRefreshing = true
                        showLoading()
                    }
                }
            }
        })

        viewModel.acceptContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_accepted_successfully, R.drawable.correct
                        ) {
                            viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })

        viewModel.acceptAmendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        refreshData()
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })

        viewModel.amendContractLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.llAmendReqyestLayout.llMainLayout.visibility = View.GONE
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.contract_amend_request_sent_successfully, R.drawable.correct
                        ) {
                            refreshData()
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })

        viewModel.updateContractStatusLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        refreshData()
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })

        viewModel.submitRatingLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.llRatingData.ratingLayout.visibility = View.GONE
                        binding.btnRateProject.visibility = View.GONE
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.you_have_submitted_rating_successfully, R.drawable.correct
                        ) {
                            refreshData()
                        }
                    }
                    Status.ERROR -> {
                        it.message?.apply {
                            showError(requireContext(), this)
                            findNavController().popBackStack()
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                }
            }
        })
    }

    private fun setUpAcceptRequestAdapter(acceptedByItems: java.util.ArrayList<AcceptedByItem>) {
        var layoutManager = LinearLayoutManager(requireContext())
        binding.rvAcceptedRequest.layoutManager = layoutManager
        binding.rvAcceptedRequest.setDivider(R.drawable.recyclerview_divider)
        var acceptedRequestAdaper = AcceptRequestAdapter(acceptedByItems, requireContext(), this)
        binding.rvAcceptedRequest.adapter = acceptedRequestAdaper
    }

    private fun setUpAmendRequestAdapter(amendRequestItems: java.util.ArrayList<AmendRequestItem>) {
        var layoutManager = LinearLayoutManager(requireContext())
        binding.rvAmendRequest.layoutManager = layoutManager
        binding.rvAmendRequest.setDivider(R.drawable.recyclerview_divider)
        var amendRequestAdapter = AmendRequestAdapter(amendRequestItems, requireContext(), this)
        binding.rvAmendRequest.adapter = amendRequestAdapter
    }

    fun acceptContract(buttonType: String) {
        when (buttonType) {
            "Accept" -> {
                val model = viewModel.acceptContractRequestModel.value
                model?.status = ApiConstant.CONTRACT_ACCEPTED
                model?.acceptedPrice = (binding.data?.price ?: "0.0").toDouble()
                model?.contract = binding.data?.id
                viewModel.validContractAcceptData()
            }
            "Start Contract" -> {
                var model = viewModel.updateContractStatusRequestModel.value
                model?.contractStatus = "IN_PROGRESS"
                viewModel.callUpdateContractApi(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
            }
        }
    }

    fun navigateToAmendDialog(buttonType: String) {
        when (buttonType) {
            "Amend" -> {
                var action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToAmendRequestFragment(
                    ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId,
                    "Amend"
                )
                findNavController().navigate(action)
            }
            "Complete Contract" -> {
                var model = viewModel.updateContractStatusRequestModel.value
                model?.contractStatus = "COMPLETED"
                viewModel.callUpdateContractApi(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
            }
        }
    }

    fun amendContract() {
        val model = viewModel.amendContractRequestModel.value
        model?.contract = ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId
        var amount = if (binding.llAmendReqyestLayout.etPrice.text.toString()
                .isEmpty()
        ) 0.0 else binding.llAmendReqyestLayout.etPrice.text?.trim().toString().toDouble()
        model?.amount = String.format("%.2f", amount).toDouble()
        viewModel.validateContractAmendData()
    }

    fun closeDialog() {
        binding.llAmendReqyestLayout.llMainLayout.visibility = View.GONE
    }

    fun refreshData() {
        viewModel.getContractDetails(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId)
    }

    override fun onButtonCLickCallback(data: AmendRequestItem, type: String) {
        if (type == "PAY_NOW") {
            val model = viewModel.acceptContractRequestModel.value
            model?.status = ApiConstant.CONTRACT_ACCEPTED
            model?.acceptedPrice = (data.amount ?: "0.0").toDouble()
            model?.contract = binding.data?.id
            viewModel.validContractAcceptData()
        } else {
            var action =
                ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToAmendRequestFragment(data.id ?: 0, type)
            findNavController().navigate(action)
        }
    }

    fun navigateToRatingFragment() {
        binding.llRatingData.ratingLayout.visibility = View.VISIBLE
        binding.btnRateProject.visibility = View.GONE
        binding.llRatingData.frag = this
    }

    fun navigateToSeeRatingFragment(data: ResultsItem) {
        var action = ProjectDetailsFragmentDirections.actionProjectDetailsFragmentToRatingFragment(
            "ProjectDetails",ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId,Gson().toJson(data.rating)
        )
        findNavController().navigate(action)
    }

    private fun setUpDetailsPageData(data: ResultsItem) {
        if (data.contractStatus == "COMPLETED" && data.createdBy?.id != UserCache.getUserId(requireContext())) {
            if (!data.rating.isNullOrEmpty()) {
                for (ratingList in data.rating) {
                    if (ratingList?.ratedBy?.id == UserCache.getUserId(requireContext())) {
                        binding.btnRateProject.visibility = View.GONE
                        binding.clRating.visibility = View.VISIBLE
                        setRatingData(ratingList)
                        return
                    } else {
                        binding.btnRateProject.visibility = View.VISIBLE
                        binding.clRating.visibility = View.GONE
                    }
                }
            }else{
                binding.btnRateProject.visibility = View.VISIBLE
                binding.clRating.visibility = View.GONE
            }
        }
    }

    private fun setRatingData(data: RatingItem?) {
        binding.tvRatingMessage.text = data?.rating.toString() + "/5.0"
        binding.tvRatingMessageVal.text = data?.ratingReason
    }

    private var selectedRating: Double  = 0.0
    fun submitRating(){
        if(selectedRating == 0.0){
            showError(requireContext(), getString(R.string.please_select_rating))
            return
        }
        if(binding.llRatingData.etRatingMessage.text.toString().trim().isNullOrBlank()){
            showError(requireContext(), getString(R.string.please_enter_rating_message))
            return
        }
        var model = viewModel.submitContractRatingRequestModel.value
        model?.rating = selectedRating
        model?.ratingReason = binding.llRatingData.etRatingMessage.text.toString().trim()
        viewModel.submitContractRating(ProjectDetailsFragmentArgs.fromBundle(requireArguments()).contractId, model!!)
    }

    fun closeRatingDialog(){
        binding.llRatingData.ratingLayout.visibility = View.GONE
        binding.btnRateProject.visibility = View.VISIBLE
    }
}