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
import com.primapp.cache.UserCache
import com.primapp.constants.MentorshipRequestActionType
import com.primapp.databinding.FragmentMentorRequestRejectionBinding
import com.primapp.extensions.showError
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*


class MentorRequestRejectionFragment : BaseFragment<FragmentMentorRequestRejectionBinding>() {

    private var requestId: Int? = null
    lateinit var type: String
    var communityData: CommunityData? = null

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
        type = MentorRequestRejectionFragmentArgs.fromBundle(requireArguments()).type
        communityData = MentorRequestRejectionFragmentArgs.fromBundle(requireArguments()).communityData

        when (type) {
            MENTEE_END_RELATION -> {
                //Mentee ending means -> appears in mentor list
                binding.rbCantAccept.text = getString(R.string.mentee_reason_relation_not_fit)
                binding.rbVacation.text = getString(R.string.mentee_reason_expertise_not_aligned)
                binding.rbLeavingCommunity.text = getString(R.string.mentee_reason_referred_to_new_mentor)
                binding.rbOthers.text = getString(R.string.other)
            }
            MENTOR_END_RELATION -> {
                //Mentor ending means -> appears in mentee list
                binding.rbCantAccept.text = getString(R.string.mentor_end_reason_max_learning)
                binding.rbVacation.text = getString(R.string.mentor_end_reason_relation_not_fit)
                binding.rbLeavingCommunity.text = getString(R.string.mentor_end_reason_untenable)
                binding.rbOthers.text = getString(R.string.other)
            }
            LEAVE_COMMUNITY -> {
                //Leave community reasons
                binding.rbCantAccept.text = getString(R.string.leave_reason_relation_not_fit)
                binding.rbVacation.text = getString(R.string.leave_reason_expertise_not_aligned)
                binding.rbLeavingCommunity.text = getString(R.string.leave_reason_refered_to_mentor)
                binding.rbOthers.text = getString(R.string.other)

                binding.ratingBar.isVisible = true
            }
        }

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
                        if (type == MENTEE_END_RELATION) {
                            UserCache.decrementMentorCount(requireContext())
                        }

                        if (type == MENTOR_END_RELATION) {
                            UserCache.decrementMenteeCount(requireContext())
                        }

                        val messageId = if (type == MENTORSHIP_REQUEST_REJECT) {
                            R.string.mentor_request_rejection_message
                        } else {
                            R.string.mentor_relation_end_message
                        }

                        DialogUtils.showCloseDialog(requireActivity(), messageId) {
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

        viewModel.leaveCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { response ->
                hideLoading()
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.content?.apply {
                            communityData?.isJoined = isJoined
                            communityData?.totalActiveMember = totalActiveMember
                            if (isJoined == true) {
                                UserCache.incrementJoinedCommunityCount(requireContext())
                            } else {
                                UserCache.decrementJoinedCommunityCount(requireContext())
                            }

                            findNavController().popBackStack()
                        }
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.ERROR -> {
                        showError(requireContext(), response.message!!)
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
            val selectedId: Int = binding.rgRejectReason.checkedRadioButtonId
            val radioButton = view?.findViewById<RadioButton>(selectedId)
            sendRejectRequest(radioButton?.text.toString())
        }
    }

    private fun sendRejectRequest(reason: String) {
        if (binding.ratingBar.isVisible && binding.ratingBar.rating == 0.0f) {
            showError(requireContext(), getString(R.string.please_give_rating))
            return
        }

        when (type) {
            MENTORSHIP_REQUEST_REJECT -> {
                viewModel.acceptRejectMentorship(requestId!!, MentorshipRequestActionType.REJECT, reason)
            }
            MENTEE_END_RELATION, MENTOR_END_RELATION -> {
                viewModel.acceptRejectMentorship(requestId!!, MentorshipRequestActionType.END, reason)
            }
            LEAVE_COMMUNITY -> {
                viewModel.leaveCommunity(
                    communityData!!.id,
                    UserCache.getUserId(requireContext()),
                    reason,
                    binding.ratingBar.rating.toDouble()
                )
            }
        }
    }

    companion object {
        const val MENTORSHIP_REQUEST_REJECT = "RejectCommunityAction"
        const val MENTEE_END_RELATION = "EndRelationshipForMentorship"
        const val MENTOR_END_RELATION = "endRelationofMentor"
        const val LEAVE_COMMUNITY = "leaveCommunity"
    }
}