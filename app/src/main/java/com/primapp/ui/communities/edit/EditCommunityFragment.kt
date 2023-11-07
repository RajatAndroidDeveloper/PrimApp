package com.primapp.ui.communities.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentEditCommunityBinding
import com.primapp.extensions.loadCircularImage
import com.primapp.extensions.loadCircularImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.post.create.adapter.AutoCompleteCategoryArrayAdapter
import com.primapp.utils.AwsHelper
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.utils.RetrofitUtils
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File

class EditCommunityFragment : BaseFragment<FragmentEditCommunityBinding>() {
    var parentCategoryId: Int = -1

    var imageFile: File? = null

    lateinit var communityData: CommunityData

    val viewModel by viewModels<EditCommunityViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_edit_community

    val categoryAdapter by lazy {
        AutoCompleteCategoryArrayAdapter(
            requireContext(),
            R.layout.item_simple_text
        )
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.edit_community), toolbar)
        setData()
        setUpCategoryAdapter()
        setObserver()
        viewModel.getParentCategoriesList(0, 1000)
    }

    private fun setData() {
        EditCommunityFragmentArgs.fromBundle(requireArguments()).let {
            binding.mAutoCompleteCategory.setText(it.communityData.communityCategory)
            communityData = it.communityData
        }

        viewModel.editCommunityRequestModel.value?.apply {
            communityName = communityData.communityName
            communityDescription = communityData.communityDescription
            status = communityData.status
            category = parentCategoryId
        }

        binding.ivProfilePic.loadCircularImage(requireContext(), communityData.imageUrl)

        binding.frag = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.editCommunityLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                hideLoading()
                when (it.status) {
                    Status.LOADING -> {
                        showLoading()
                    }

                    Status.SUCCESS -> {
                        it.data?.content?.apply {
                            communityData.communityName = communityName
                            communityData.communityDescription = communityDescription
                            communityData.status = status
                            communityData.imageUrl = imageUrl
                            communityData.totalActiveMember = totalActiveMember
                        }

                        DialogUtils.showCloseDialog(requireActivity(), R.string.changes_saved_successfully) {
                            findNavController().popBackStack()
                        }
                    }

                    Status.ERROR -> {
                        showError(requireContext(), it.message!!)
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
                            viewModel.editCommunityRequestModel.value?.communityImageFile = it.fields.key
                            viewModel.uploadAWS(
                                it.url,
                                it.fields.key?:"",
                                it.fields.aWSAccessKeyId?:"",
                                it.fields.xAmzSecurityToken,
                                it.fields.policy?:"",
                                it.fields.signature?:"",
                                it.fields.xAmzAlgorithm?:"",
                                it.fields.xAmzCredential?:"",
                                it.fields.xAmzDate?:"",
                                it.fields.xAmzSignature?:"",
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
                        viewModel.editCommunity(communityData.id)
                    }
                }
            }
        })

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
    }

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
                        binding.ivProfilePic.loadCircularImageWithoutCache(imageFile!!.absolutePath)
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${imageFile!!.absolutePath}")
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }

                }
            }
        }
    }

    fun onToggleStatus() {
        if (!binding.scCommunityStatus.isChecked) {
            DialogUtils.showYesNoDialog(
                requireActivity(),
                R.string.community_inactive_status_message,
                yesClickCallback = {
                    changeStatus(CommunityStatusTypes.INACTIVE)
                }, noClickCallback = {
                    changeStatus(CommunityStatusTypes.ACTIVE)
                })
        } else {
            changeStatus(CommunityStatusTypes.ACTIVE)
        }
    }

    private fun changeStatus(status: String) {
        val data = viewModel.editCommunityRequestModel.value
        data?.status = status
        viewModel.editCommunityRequestModel.value = data
    }

    fun save() {
        if (viewModel.validateEditCommunity()) {
            if (imageFile != null) {
                viewModel.generatePresignedUrl(
                    AwsHelper.getObjectName(
                        AwsHelper.AWS_OBJECT_TYPE.COMMUNITY,
                        communityData.id,
                        imageFile!!.extension,
                    )
                )
            } else {
                viewModel.editCommunity(communityData.id)
            }
        }
    }

    fun cancel() {
        findNavController().popBackStack()
    }

    private fun setUpCategoryAdapter() {
        context.apply {
            binding.mAutoCompleteCategory.setAdapter(categoryAdapter)
            binding.mAutoCompleteCategory.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = categoryAdapter.contains(p0.toString())
                    if (isDataValid) {
                        parentCategoryId = categoryAdapter.getItemId(p0.toString())?:-1
                        val data = viewModel.editCommunityRequestModel.value
                        data?.category = parentCategoryId
                        viewModel.editCommunityRequestModel.value = data
                        parentCategoryId.let {
                            binding.tlSelectCategory.error = null
                        }
                    } else {
                        parentCategoryId = -1
                    }

                    return isDataValid
                }
            }

            binding.mAutoCompleteCategory.setOnItemClickListener { adapterView, view, i, l ->
                binding.mAutoCompleteCategory.clearFocus()
            }
        }

        binding.mAutoCompleteCategory.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                val itemData = categoryAdapter.getItem(pos)
                parentCategoryId = itemData.id
                val data = viewModel.editCommunityRequestModel.value
                data?.category = parentCategoryId
                viewModel.editCommunityRequestModel.value = data
                parentCategoryId.let {
                    binding.tlSelectCategory.error = null
                }
            }
    }

}