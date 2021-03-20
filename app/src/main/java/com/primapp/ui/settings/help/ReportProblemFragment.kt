package com.primapp.ui.settings.help

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.databinding.FragmentReportProblemBinding
import com.primapp.extensions.loadImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.AwsHelper
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.utils.RetrofitUtils
import com.primapp.viewmodels.ReportIssueViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File

class ReportProblemFragment() : BaseFragment<FragmentReportProblemBinding>() {

    var imageFile: File? = null

    val viewModel by viewModels<ReportIssueViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_report_problem

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.report_a_problem), toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {

        viewModel.reportIssueLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message.toString())
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.report_problem_success) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.generatePresignedURLLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message.toString())
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            viewModel.reportIssueRequestModel.value?.issueImageFile = it.fields.key
                            viewModel.uploadAWS(
                                it.url,
                                it.fields.key,
                                it.fields.aWSAccessKeyId,
                                it.fields.xAmzSecurityToken,
                                it.fields.policy,
                                it.fields.signature,
                                RetrofitUtils.fileToRequestBody(File(imageFile!!.absolutePath), "file")
                            )
                        }
                    }
                }
            }
        })

        viewModel.uploadAWSLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.ERROR -> {
                        showError(requireContext(), it.message.toString())
                    }
                    Status.LOADING -> {
                        showLoading()
                    }
                    Status.SUCCESS -> {
                        viewModel.reportIssue()
                    }
                }
            }
        })
    }

    fun reportIssue() {
        val message = binding.etIssueText.text.toString()
        if (message.isNotEmpty()) {
            if (imageFile != null) {
                viewModel.generatePresignedUrl(
                    AwsHelper.getObjectName(
                        AwsHelper.AWS_OBJECT_TYPE.ISSUE,
                        UserCache.getUserId(requireContext()),
                        imageFile!!.extension
                    )
                )
            } else {
                viewModel.reportIssue()
            }
        } else {
            binding.etIssueText.error = getString(R.string.valid_issue_text)
            binding.etIssueText.requestFocus()
        }
    }

    //-----Image pic logic---

    fun pickImageAskPermission() {
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            pickImage()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun pickImage() {
        startActivityForResult(FileUtils.getPickImageIntent(context), FileUtils.IMAGE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isGranted = isAllPermissionsGranted(grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (isGranted) {
                    pickImage()
                } else {
                    val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                    if (!showRationale) {
                        /*
                        *   Permissions are denied permanently, redirect to permissions page
                        * */
                        showError(requireContext(), getString(R.string.error_camera_permission))
                        redirectUserToAppSettings()
                    } else {
                        DialogUtils.showCloseDialog(
                            requireActivity(),
                            R.string.error_camera_permission,
                            R.drawable.question_mark
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FileUtils.IMAGE_REQUEST_CODE -> {
                    if (data?.data != null) {
                        //Photo from gallery.
                        imageFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.IMAGE)
                    } else {
                        //Photo from camera.
                        imageFile = FileUtils.getFile(context, FileUtils.IMAGE)
                    }

                    if (imageFile != null) {
                        FileUtils.compressImage(imageFile!!.absolutePath)
                        binding.ivIssueImage.loadImageWithoutCache(imageFile!!.absolutePath)
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${imageFile!!.absolutePath}")
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }

                }
            }
        }
    }
}