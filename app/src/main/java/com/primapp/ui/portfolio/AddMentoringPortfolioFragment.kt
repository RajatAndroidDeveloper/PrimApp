package com.primapp.ui.portfolio

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.PostFileType
import com.primapp.databinding.FragmentAddMentoringPortfolioBinding
import com.primapp.extensions.showError
import com.primapp.model.DeleteItem
import com.primapp.model.DownloadFile
import com.primapp.model.ShowImage
import com.primapp.model.ShowVideo
import com.primapp.model.portfolio.PortfolioContent
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.portfolio.adapter.AddMentoringPortfolioAdapter
import com.primapp.utils.*
import com.primapp.viewmodels.PortfolioViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class AddMentoringPortfolioFragment : BaseFragment<FragmentAddMentoringPortfolioBinding>() {

    var selectedFile: File? = null
    var fileType: String? = null
    var isThumbnailUploaded: Boolean = false
    var isShowRemove: Boolean = false

    @Inject
    lateinit var downloadManager: DownloadManager

    lateinit var portfolioContent: PortfolioContent

    private val adapter by lazy { AddMentoringPortfolioAdapter { item -> onItemClick(item) } }

    val viewModel by viewModels<PortfolioViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_add_mentoring_portfolio

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.mentoring_portfolio), toolbar)
        setAdapter()
        setData()
        setObserver()
    }

    fun setData() {
        binding.frag = this
        binding.isShowRemove = isShowRemove
        portfolioContent = AddMentoringPortfolioFragmentArgs.fromBundle(requireArguments()).portfolioData

        portfolioContent.mentoringPortfolio?.let {
            adapter.addData(it)
        }
    }

    private fun setAdapter() {
        binding.rvMentoring.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        binding.rvMentoring.adapter = adapter
    }

    fun onToggleRemove() {
        isShowRemove = !isShowRemove
        adapter.toggleRemoveButton()
        binding.isShowRemove = isShowRemove
    }

    private fun setObserver() {
        viewModel.addMentoringPortfolioLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.let {
                            adapter.addItem(it.content)
                            //Update list to avoid api call
                            portfolioContent.mentoringPortfolio = adapter.list
                        }
                    }
                }
            }
        })

        viewModel.deleteMentoringPortfolioLiveData.observe(viewLifecycleOwner, Observer {
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
                        it.data?.content?.let {
                            adapter.removeItem(it.id)
                            //Update list to avoid api call
                            portfolioContent.mentoringPortfolio = adapter.list
                        }
                    }
                }
            }
        })

        //------To Upload files------
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
                                viewModel.mentoringPortfolioRequestModel.value?.thumbnailFile = it.fields.key
                                val bitmap = FileUtils.getBitmapThumbnailForVideo(requireContext(), selectedFile!!)
                                part = RetrofitUtils.bitmapToMultipartBody(bitmap, it.fields.key, "file")
                            } else {
                                viewModel.mentoringPortfolioRequestModel.value?.contentFile = it.fields.key
                                part = RetrofitUtils.fileToRequestBody(selectedFile!!, "file")
                            }

                            viewModel.mentoringPortfolioRequestModel.value?.fileType = fileType
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
                        if (fileType == PostFileType.VIDEO && !isThumbnailUploaded) {
                            isThumbnailUploaded = true
                            viewModel.generatePresignedUrl(
                                AwsHelper.getObjectName(
                                    AwsHelper.AWS_OBJECT_TYPE.THUMBNAIL,
                                    UserCache.getUser(requireContext())!!.id,
                                    "jpg"
                                )
                            )
                        } else {
                            //File Uploaded Send request to create mentoring portfolio
                            sendRequestToAddPortfolio()
                        }
                    }
                }
            }
        })
    }

    private fun sendRequestToAddPortfolio() {
        viewModel.addMentoringPortfolio()
    }

    fun attachFileAndSend() {
        if (selectedFile != null) {
            if (fileType.equals(PostFileType.FILE)) {
                // Use original name in case of File attachment
                viewModel.generatePresignedUrl(selectedFile!!.name)
            } else {
                viewModel.generatePresignedUrl(
                    AwsHelper.getObjectName(
                        AwsHelper.AWS_OBJECT_TYPE.PORTFOLIO,
                        UserCache.getUser(requireContext())!!.id,
                        selectedFile!!.extension
                    )
                )
            }
        }
    }

    private fun onItemClick(item: Any?) {
        when (item) {
            is ShowImage -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.imageViewDialog, bundle)
            }
            is ShowVideo -> {
                val bundle = Bundle()
                bundle.putString("url", item.url)
                findNavController().navigate(R.id.videoViewDialog, bundle)
            }
            is DownloadFile -> {
                DownloadUtils.download(requireContext(), downloadManager, item.url)
            }
            is DeleteItem -> {
                DialogUtils.showYesNoDialog(requireActivity(), R.string.remove_portfolio_msg, {
                    viewModel.deleteMentoringPortfolio(item.id)
                })
            }
            null -> {
                addMore()
            }
        }
    }

    fun addMore() {
        val fileTypeOptions = arrayOf("Image", "Video", "File")
        DialogUtils.showChooserDialog(requireContext(), getString(R.string.choose_media_type), fileTypeOptions) {
            if (it == 0) {
                fileType = PostFileType.IMAGE
            } else if (it == 1) {
                fileType = PostFileType.VIDEO
            } else {
                fileType = PostFileType.FILE

            }
            pickFileAskPermission()
            viewModel.mentoringPortfolioRequestModel.value?.fileType = fileType
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

    private fun selectFile() {
        when (fileType) {
            PostFileType.IMAGE, PostFileType.GIF -> {
                startActivityForResult(FileUtils.getPickImageIntent(context), FileUtils.IMAGE_REQUEST_CODE)
            }
            PostFileType.VIDEO -> {
                startActivityForResult(FileUtils.getPickVideoIntent(context), FileUtils.VIDEO_REQUEST_CODE)
            }
            PostFileType.FILE -> {
                startActivityForResult(FileUtils.getDocumentFileIntent(), FileUtils.FILE_REQUEST_CODE)
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
                            fileType = PostFileType.GIF
                            selectedFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.GIF)
                        } else {
                            fileType = PostFileType.IMAGE
                            selectedFile = FileUtils.getFileFromUri(context, data.data!!, FileUtils.IMAGE)
                            FileUtils.compressImage(selectedFile!!.absolutePath)
                        }

                    } else {
                        //Photo from camera.
                        selectedFile = FileUtils.getFile(context, FileUtils.IMAGE)
                        FileUtils.compressImage(selectedFile!!.absolutePath)
                    }

                    if (selectedFile != null) {
                        attachFileAndSend()
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
                            showError(requireContext(), getString(R.string.video_file_size_error_message))
                        } else {
                            selectedFile = tempFile
                            attachFileAndSend()
                        }
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }
                }

                FileUtils.FILE_REQUEST_CODE -> {
                    var tempFile: File? = null
                    if (data?.data == null) {
                        tempFile = FileUtils.getFile(requireContext(), FileUtils.IMAGE)
                    } else {
                        tempFile = FileUtils.getFileFromUri(requireContext(), data.data!!)
                    }

                    if (tempFile != null && tempFile.exists()) {
                        val fileSize = (tempFile.length() / 1024) / 1024
                        if (fileSize > 18) {
                            showError(requireContext(), getString(R.string.video_file_size_error_message))
                        } else {
                            selectedFile = tempFile
                            attachFileAndSend()
                        }
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }
                }

            }
        } else {
            Log.d(FileUtils.FILE_PICK_TAG, "File type and selected file Cleared")
            fileType = null
            selectedFile = null
            //For update image
            viewModel.mentoringPortfolioRequestModel.value?.fileType = fileType
        }
    }
}