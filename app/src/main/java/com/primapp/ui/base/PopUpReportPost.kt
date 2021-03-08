package com.primapp.ui.base

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.primapp.R
import com.primapp.constants.ReportReasonTypes
import com.primapp.databinding.LayoutReportPostPopUpBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showSuccess
import com.primapp.model.post.PostListResult
import com.primapp.retrofit.base.Status
import com.primapp.ui.splash.SplashViewModel

class PopUpReportPost : BaseDialogFragment<LayoutReportPostPopUpBinding>() {

    lateinit var postData: PostListResult

    val viewModel by viewModels<SplashViewModel> { viewModelFactory }

    private var reportType = ReportReasonTypes.RUDE_LANGUAGE

    override fun getLayoutRes(): Int = R.layout.layout_report_post_pop_up

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setData()
        setObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setObserver() {
        viewModel.reportPostLiveData.observe(viewLifecycleOwner, Observer {
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
                        showSuccess(requireContext(), "Post reported successfully")
                        dismiss()
                    }
                }
            }
        })
    }

    private fun setData() {
        postData = PopUpReportPostArgs.fromBundle(requireArguments()).postData

        binding.pop = this
        isCancelable = false

        binding.rgReportOptions.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbRudeLanguage -> {
                        reportType = ReportReasonTypes.RUDE_LANGUAGE
                        binding.etReason.hint = getString(R.string.would_you_like_to_describe)
                    }

                    R.id.rbSexualContent -> {
                        reportType = ReportReasonTypes.SEXUAL_CONTENT
                        binding.etReason.hint = getString(R.string.would_you_like_to_describe)
                    }
                    R.id.rbHarassment -> {
                        reportType = ReportReasonTypes.HARASSMENT
                        binding.etReason.hint = getString(R.string.would_you_like_to_describe)
                    }
                    R.id.rbViolent -> {
                        reportType = ReportReasonTypes.THREAT_OR_VIOLENT
                        binding.etReason.hint = getString(R.string.would_you_like_to_describe)
                    }

                    R.id.rbOthers -> {
                        reportType = ReportReasonTypes.OTHERS
                        binding.etReason.hint = getString(R.string.mention_your_reason_here)
                    }
                }
            }
        })
    }

    fun reportPost() {
        val reasonText = binding.etReason.text.toString()
        if (binding.rgReportOptions.checkedRadioButtonId == R.id.rbOthers && reasonText.isEmpty()) {
            binding.etReason.error = getString(R.string.error_reason)
            binding.etReason.requestFocus()
        } else {
            viewModel.reportPost(postData.community.id, postData.id, reportType, reportText = reasonText)
        }
    }
}