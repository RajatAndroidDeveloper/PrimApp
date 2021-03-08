package com.primapp.ui.notification

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.databinding.FragmentMentorRequestRejectionBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_mentor_request_rejection.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class MentorRequestRejectionFragment : BaseFragment<FragmentMentorRequestRejectionBinding>() {

    private var requestId: Int? = null

    val viewModel by viewModels<NotificationViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_mentor_request_rejection

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.select_a_reason), toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        requestId = MentorRequestRejectionFragmentArgs.fromBundle(requireArguments()).requestId

        binding.rgRejectReason.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbCantAccept -> {
                        binding.etReason.isVisible = false
                    }
                    R.id.rbVacation -> {
                        binding.etReason.isVisible = false
                    }
                    R.id.rbLeavingCommunity -> {
                        binding.etReason.isVisible = false
                    }
                    R.id.rbOthers -> {
                        binding.etReason.isVisible = true
                    }
                }
            }
        })
    }

    private fun setObserver() {
        viewModel.acceptRejectMentorshipLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.mentor_request_rejection_message) {
                            findNavController().popBackStack()
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
                    }
                }
            }
        })
    }

    fun submitReason() {
        if (binding.etReason.isVisible) {
            val text: String = binding.etReason.text.toString()
            if (text.isNotEmpty()) {
                sendRejectRequest(text)
            } else {
                binding.etReason.error = getString(R.string.error_reason)
                binding.etReason.requestFocus()
            }
        } else {
            val selectedId: Int = rgRejectReason.checkedRadioButtonId
            val radioButton = view?.findViewById<RadioButton>(selectedId)
            sendRejectRequest(radioButton?.text.toString())
        }
    }

    private fun sendRejectRequest(reason: String) {
        viewModel.acceptRejectMentorship(requestId!!, MentorshipRequestActionType.REJECT, reason)
    }
}