package com.primapp.ui.post.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.PostFileType
import com.primapp.databinding.FragmentCreatePostBinding
import com.primapp.extensions.showError
import com.primapp.extensions.showInfo
import com.primapp.model.community.JoinedCommunityListModel
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.post.create.adapter.AutocompleteCommunityArrayAdapter
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File


class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    var imageFile: File? = null

    var postFileType: String? = null

    var selectedCommunityId: Int? = null

    var joinedCommunityResponse: JoinedCommunityListModel? = null

    val communityAdapter by lazy { AutocompleteCommunityArrayAdapter(requireContext(), R.layout.item_simple_text) }

    val viewModel by viewModels<CreatePostViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_create_post

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.create_post), toolbar)
        setAdapter()
        setData()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel

        joinedCommunityResponse = CreatePostFragmentArgs.fromBundle(
            requireArguments()
        ).joinedCommunityResponse

        joinedCommunityResponse?.let {
            communityAdapter.addAll(it.content)
        }

        binding.rgFileType.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbImage -> {
                        postFileType = PostFileType.IMAGE
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbVideo -> {
                        postFileType = PostFileType.VIDEO
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbGif -> {
                        postFileType = PostFileType.GIF
                        binding.groupSelectFile.isVisible = true
                    }
                    R.id.rbNone -> {
                        postFileType = null
                        binding.groupSelectFile.isVisible = false
                    }
                }
            }
        })
    }

    private fun setObserver() {
        viewModel.createPostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        DialogUtils.showCloseDialog(requireActivity(), R.string.post_created_success) {
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

    private fun setAdapter() {
        context?.apply {
            binding.mAutoCompleteCommunity.setAdapter(communityAdapter)

            binding.mAutoCompleteCommunity.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = communityAdapter.contains(p0.toString())
                    if (isDataValid) {
                        selectedCommunityId = communityAdapter.getItemId(p0.toString())
                    } else {
                        selectedCommunityId = null
                    }

                    return isDataValid
                }
            }
        }
    }

    fun createPost() {
        binding.mAutoCompleteCommunity.clearFocus()
        if (selectedCommunityId != null) {
            binding.tlSelectCommunity.error = null
            if (postFileType == null && binding.etPost.text.isNotEmpty()) {
                binding.etPost.error = null
                viewModel.createPost(selectedCommunityId!!, UserCache.getUser(requireContext())!!.id)
            } else if (postFileType != null) {
                showInfo(requireContext(), "${postFileType} upload is not implemented yet")
            } else {
                binding.etPost.error = "Text can't be empty"
            }
        } else {
            binding.tlSelectCommunity.error = "Please select community"
        }
    }

    /*--------- File Picker Code ----------*/

    fun pickFileAskPermission() {
        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            selectFile()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun selectFile() {
        when (postFileType) {
            PostFileType.IMAGE -> {
                startActivityForResult(FileUtils.getPickImageIntent(context), FileUtils.IMAGE_REQUEST_CODE)
            }
            PostFileType.VIDEO -> {
                startActivityForResult(FileUtils.getPickVideoIntent(context), FileUtils.VIDEO_REQUEST_CODE)
            }
        }
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
                    selectFile()
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
                        imageFile = FileUtils.getFileFromUri(context, data.data!!)
                    } else {
                        //Photo from camera.
                        imageFile = FileUtils.getImageFile(context)
                    }

                    if (imageFile != null) {
                        binding.tvFileName.text = "${imageFile!!.name}"
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${imageFile!!.absolutePath}")
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }

                }

                FileUtils.VIDEO_REQUEST_CODE -> {
                    if (data?.data != null) {
                        Log.e(FileUtils.FILE_PICK_TAG, "Video : ${Gson().toJson(data?.data)}")
                    }
                }
            }
        }
    }
}
