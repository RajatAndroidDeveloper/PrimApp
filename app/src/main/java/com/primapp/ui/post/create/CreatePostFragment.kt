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
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.PostFileType
import com.primapp.databinding.FragmentCreatePostBinding
import com.primapp.extensions.showError
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.post.create.adapter.AutoCompleteCategoryArrayAdapter
import com.primapp.ui.post.create.adapter.AutocompleteCommunityArrayAdapter
import com.primapp.utils.*
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import okhttp3.MultipartBody
import java.io.File


class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    var selectedFile: File? = null

    var isThumbnailUploaded: Boolean = false

    var postFileType: String? = null

    var selectedCategoryId: Int? = null

    var selectedCommunityId: Int? = null

    val communityAdapter by lazy { AutocompleteCommunityArrayAdapter(requireContext(), R.layout.item_simple_text) }

    val categoryAdapter by lazy { AutoCompleteCategoryArrayAdapter(requireContext(), R.layout.item_simple_text) }

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

        binding.rgFileType.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                when (p1) {
                    R.id.rbImage -> {
                        selectedFile = null
                        postFileType = PostFileType.IMAGE
                        binding.btnSelect.isVisible = true
                        binding.groupSelectFileName.isVisible = false
                    }
                    R.id.rbVideo -> {
                        selectedFile = null
                        postFileType = PostFileType.VIDEO
                        binding.btnSelect.isVisible = true
                        binding.groupSelectFileName.isVisible = false
                    }
                    R.id.rbGif -> {
                        selectedFile = null
                        postFileType = PostFileType.GIF
                        binding.btnSelect.isVisible = true
                        binding.groupSelectFileName.isVisible = false
                    }
                    R.id.rbNone -> {
                        selectedFile = null
                        postFileType = null
                        binding.btnSelect.isVisible = false
                        binding.groupSelectFileName.isVisible = false
                    }
                }
            }
        })
    }

    private fun setObserver() {

        viewModel.parentCategoryLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.results?.let {
                            categoryAdapter.addAll(it)
                        }
                    }
                }
            }
        })

        viewModel.getParentCategoriesList(0, 1000)

        viewModel.categoryJoinedCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.tlSelectCommunity.isVisible = true
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
                            communityAdapter.addAll(it)
                        }
                    }
                }
            }
        })


        viewModel.createPostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        UserCache.incrementPostCount(requireContext())
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
                            val part: MultipartBody.Part
                            if (isThumbnailUploaded) {
                                viewModel.createPostRequestModel.value?.thumbnailFile = it.fields.key
                                val bitmap = FileUtils.getBitmapThumbnailForVideo(requireContext(), selectedFile!!)
                                part = RetrofitUtils.bitmapToMultipartBody(bitmap, it.fields.key, "file")
                            } else {
                                viewModel.createPostRequestModel.value?.postContentFile = it.fields.key
                                part = RetrofitUtils.fileToRequestBody(selectedFile!!, "file")
                            }

                            viewModel.createPostRequestModel.value?.fileType = postFileType
                            viewModel.uploadAWS(
                                it.url,
                                it.fields.key,
                                it.fields.aWSAccessKeyId,
                                it.fields.xAmzSecurityToken,
                                it.fields.policy,
                                it.fields.signature,
                                part
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
                        if (postFileType == PostFileType.VIDEO && !isThumbnailUploaded) {
                            isThumbnailUploaded = true
                            viewModel.generatePresignedUrl(
                                AwsHelper.getObjectName(
                                    AwsHelper.AWS_OBJECT_TYPE.THUMBNAIL,
                                    UserCache.getUser(requireContext())!!.id,
                                    "jpg"
                                )
                            )
                        } else {
                            sendPost()
                        }
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
                        selectedCommunityId?.let {
                            binding.tlSelectCommunity.error = null
                        }
                    } else {
                        selectedCommunityId = null
                    }

                    return isDataValid
                }
            }

            binding.mAutoCompleteCategory.setAdapter(categoryAdapter)

            binding.mAutoCompleteCategory.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = categoryAdapter.contains(p0.toString())
                    if (isDataValid) {
                        selectedCategoryId = categoryAdapter.getItemId(p0.toString())
                        selectedCategoryId?.let {
                            binding.tlSelectCategory.error = null
                            viewModel.getCategoryJoinedCommunityListData(it)
                        }
                    } else {
                        selectedCategoryId = null
                    }

                    return isDataValid
                }
            }

            binding.mAutoCompleteCategory.setOnItemClickListener { adapterView, view, i, l ->
                binding.mAutoCompleteCategory.clearFocus()
            }
            binding.mAutoCompleteCommunity.setOnItemClickListener { adapterView, view, i, l ->
                binding.mAutoCompleteCommunity.clearFocus()
            }
        }
    }

    fun createPost() {
        binding.mAutoCompleteCommunity.clearFocus()
        binding.mAutoCompleteCategory.clearFocus()
        if (selectedCategoryId != null && selectedCommunityId != null) {
            if (postFileType == null && binding.etPost.text.isNotEmpty() && Validator.isValidPostTextLength(binding.etPost.text.toString())) {
                //Text Only Post Type
                binding.etPost.error = null
                sendPost()
            } else if (postFileType != null && Validator.isValidPostTextLength(binding.etPost.text.toString())) {
                //Post with attachment
                if (selectedFile != null) {
                    viewModel.generatePresignedUrl(
                        AwsHelper.getObjectName(
                            AwsHelper.AWS_OBJECT_TYPE.POST,
                            UserCache.getUser(requireContext())!!.id,
                            selectedFile!!.extension
                        )
                    )
                } else {
                    //Post with attachment selected but attachment not attached
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        getString(R.string.file_type_error, postFileType),
                        R.drawable.question_mark
                    )
                }
            } else {
                if (binding.etPost.text.isEmpty())
                    binding.etPost.error = getString(R.string.valid_post_text)
                else
                    binding.etPost.error = getString(R.string.valid_post_text_length)
                binding.etPost.requestFocus()
            }
        } else {
            if (selectedCategoryId == null)
                binding.tlSelectCategory.error = getString(R.string.valid_select_category)
            else
                binding.tlSelectCommunity.error = getString(R.string.valid_select_community)
        }
    }

    private fun sendPost() {
        viewModel.createPost(selectedCommunityId!!, UserCache.getUser(requireContext())!!.id)
    }

    fun clearAttachment() {
        selectedFile = null
        binding.groupSelectFileName.isVisible = false
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

    private fun selectFile() {
        when (postFileType) {
            PostFileType.IMAGE, PostFileType.GIF -> {
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
                        val path = FileUtils.getPathFromURI(requireContext(), data?.data!!)
                        Log.d(FileUtils.FILE_PICK_TAG, "Path : ${path}")
                        val splitPath = path.split("/")
                        if (splitPath.last().contains(".gif")) {
                            postFileType = PostFileType.GIF
                            selectedFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.GIF)
                        } else {
                            postFileType = PostFileType.IMAGE
                            selectedFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.IMAGE)
                            FileUtils.compressImage(selectedFile!!.absolutePath)
                        }

                    } else {
                        //Photo from camera.
                        selectedFile = FileUtils.getFile(context, FileUtils.IMAGE)
                        FileUtils.compressImage(selectedFile!!.absolutePath)
                    }

                    if (selectedFile != null) {
                        binding.groupSelectFileName.isVisible = true
                        binding.tvFileName.text = "${selectedFile!!.name}"
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${selectedFile!!.absolutePath}")
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }

                }

                FileUtils.VIDEO_REQUEST_CODE -> {
                    var tempFile: File? = null
                    if (data?.data != null && !data.data?.lastPathSegment.equals("video_temp.mov")) {
                        //Photo from gallery.
                        tempFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.VIDEO)
                    } else {
                        //Photo from camera.
                        tempFile = FileUtils.getFile(context, FileUtils.VIDEO)
                    }

                    if (tempFile != null && tempFile.exists()) {
                        val fileSize = (tempFile.length() / 1024) / 1024
                        if (fileSize > 18) {
                            showError(requireContext(), "File size too big. Please upload a video with upto 18mb only")
                        } else {
                            selectedFile = tempFile
                            binding.groupSelectFileName.isVisible = true
                            binding.tvFileName.text = "${selectedFile!!.name}"
                        }
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }
                }
            }
        }
    }
}