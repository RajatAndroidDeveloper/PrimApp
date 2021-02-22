package com.primapp.ui.post.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.activity.addCallback
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
import com.primapp.model.post.PostListResult
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

    lateinit var type: String

    //incase of update post
    var postData: PostListResult? = null
    var isUpdatedPostAttachment: Boolean = false

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
        setData()
        setAdapter()
        setObserver()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel
        CreatePostFragmentArgs.fromBundle(requireArguments()).let { arguments ->
            type = arguments.type ?: CREATE_POST
            postData = arguments.postData
        }

        if (type == CREATE_POST) {
            viewModel.getParentCategoriesList(0, 1000)
        } else {
            //EDIT_POST
            tvTitle.text = getString(R.string.edit_post)
            binding.tlSelectCategory.isEnabled = false
            binding.tlSelectCommunity.isEnabled = false
            binding.tlSelectCommunity.isVisible = true
            postData?.let {
                binding.mAutoCompleteCategory.setText(it.community.category.categoryName)
                binding.mAutoCompleteCommunity.setText(it.community.communityName)
                val requestModel = viewModel.createPostRequestModel.value
                requestModel?.postText = it.postText
                requestModel?.fileType = it.fileType
                requestModel?.postContentFile = it.postContentFile
                requestModel?.thumbnailFile = it.thumbnailFile
                viewModel.createPostRequestModel.value = requestModel
                //set local variables
                selectedCategoryId = it.community.category.id
                selectedCommunityId = it.community.id
                postFileType = it.fileType
                when (it.fileType) {
                    PostFileType.IMAGE -> binding.rgFileType.check(R.id.rbImage)
                    PostFileType.GIF -> binding.rgFileType.check(R.id.rbImage)
                    PostFileType.VIDEO -> binding.rgFileType.check(R.id.rbVideo)
                    null -> binding.rgFileType.check(R.id.rbNone)
                }
                if (it.fileType != null) {
                    binding.btnSelect.isVisible = true
                    binding.groupSelectFileName.isVisible = true
                    binding.tvFileName.text = it.postContentFile
                }
            }
        }

        binding.rgFileType.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                selectedFile = null
                binding.btnSelect.isVisible = true
                binding.groupSelectFileName.isVisible = false
                when (p1) {
                    R.id.rbImage -> {
                        postFileType = PostFileType.IMAGE
                    }
                    R.id.rbVideo -> {
                        postFileType = PostFileType.VIDEO
                    }
                    R.id.rbGif -> {
                        postFileType = PostFileType.GIF
                    }
                    R.id.rbNone -> {
                        postFileType = null
                        binding.btnSelect.isVisible = false
                    }
                }
                //For update post, so that we can remove data if filetype changes in viewmodel
                viewModel.createPostRequestModel.value?.fileType = postFileType
                isUpdatedPostAttachment = true
            }
        })

        //Back button press callback
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (type == CREATE_POST) {
                val requestData = viewModel.createPostRequestModel.value
                if (!requestData?.postText.isNullOrEmpty() || selectedFile != null) {
                    DialogUtils.showYesNoDialog(requireActivity(), R.string.create_post_discard_msg, {
                        findNavController().popBackStack()
                    })
                } else {
                    findNavController().popBackStack()
                }
            } else {
                findNavController().popBackStack()
            }
        }
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

        viewModel.updatePostLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.content?.let {
                            postData?.postContentFile = it.postContentFile
                            postData?.fileType = it.fileType
                            postData?.postText = it.postText
                            postData?.thumbnailFile = it.thumbnailFile
                            postData?.imageUrl = it.imageUrl
                            postData?.getThumbnailUrl = it.getThumbnailUrl
                        }
                        DialogUtils.showCloseDialog(requireActivity(), R.string.changes_saved_successfully) {
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
                attachFileToPost()
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

    private fun attachFileToPost() {
        if (selectedFile != null) {
            viewModel.generatePresignedUrl(
                AwsHelper.getObjectName(
                    AwsHelper.AWS_OBJECT_TYPE.POST,
                    UserCache.getUser(requireContext())!!.id,
                    selectedFile!!.extension
                )
            )
        } else if (selectedFile == null && type == UPDATE_POST && !isUpdatedPostAttachment) {
            Log.d("anshul_update", "-----Not updated post attachment-----")
            sendPost()
        } else {
            //Post with attachment selected but attachment not attached
            DialogUtils.showCloseDialog(
                requireActivity(),
                getString(R.string.file_type_error, postFileType),
                R.drawable.question_mark
            )
        }
    }

    private fun sendPost() {
        if (type == CREATE_POST)
            viewModel.createPost(selectedCommunityId!!, UserCache.getUser(requireContext())!!.id)
        else {
            Log.d("anshul_update", Gson().toJson(viewModel.createPostRequestModel.value))
            viewModel.updatePost(selectedCommunityId!!, UserCache.getUser(requireContext())!!.id, postData!!.id)
        }
    }

    fun clearAttachment() {
        isUpdatedPostAttachment = true
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

    companion object {
        const val CREATE_POST = "create_post"
        const val UPDATE_POST = "update_post"
    }
}
