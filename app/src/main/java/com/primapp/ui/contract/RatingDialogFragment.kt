package com.primapp.ui.contract

import android.app.DownloadManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.databinding.FragmentRatingDialogBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseDialogFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.ContractsViewModel
import javax.inject.Inject

class RatingDialogFragment : BaseDialogFragment<FragmentRatingDialogBinding>() {
    val viewModel by viewModels<ContractsViewModel> { viewModelFactory }

    @Inject
    lateinit var downloadManager: DownloadManager
    private var selectedRating: Double  = 0.0

    override fun getLayoutRes(): Int = R.layout.fragment_rating_dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            dialog?.window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.frag = this
        attachObservers()

        binding.ratingBar.setOnRatingChangeListener { _, rating, fromUser ->
            Log.e("ratingBar", "rating:$rating -- fromUser: $fromUser")
            selectedRating = rating.toDouble()
        }
    }

    fun closeDialog() {
        if (dialog?.isShowing == true)
            dialog?.dismiss()
    }

    fun submitRating(){
        DialogUtils.showCloseDialog(
            requireActivity(),
            R.string.you_have_submitted_rating_successfully, R.drawable.correct
        ) {
            if(dialog?.isShowing == true){
                dialog?.dismiss()
            }
        }

//        if(selectedRating == 0.0){
//            showError(requireContext(), getString(R.string.please_select_rating))
//            return
//        }
//        if(binding.etRatingMessage.text.toString().trim().isNullOrBlank()){
//            showError(requireContext(), getString(R.string.please_enter_rating_message))
//            return
//        }
//        var model = viewModel.submitContractRatingRequestModel.value
//        model?.rating = selectedRating
//        model?.ratingReason = binding.etRatingMessage.text.toString().trim()
//        viewModel.submitContractRating(RatingDialogFragmentArgs.fromBundle(requireArguments()).contractId, model!!)
    }

    private fun attachObservers() {
        viewModel.submitRatingLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.you_have_submitted_rating_successfully, R.drawable.correct
                        ) {
                            findNavController().popBackStack()
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
}
