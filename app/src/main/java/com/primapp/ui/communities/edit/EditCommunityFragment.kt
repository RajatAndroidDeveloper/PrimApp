package com.primapp.ui.communities.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.constants.CommunityStatusTypes
import com.primapp.databinding.FragmentEditCommunityBinding
import com.primapp.extensions.loadCircularImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.model.community.CommunityData
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.viewmodels.CommunitiesViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File

class EditCommunityFragment : BaseFragment<FragmentEditCommunityBinding>() {

    lateinit var communityData: CommunityData

    val viewModel by viewModels<EditCommunityViewModel> { viewModelFactory }

    override fun getLayoutRes(): Int = R.layout.fragment_edit_community

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.edit_community), toolbar)
        setData()
        setObserver()
    }

    private fun setData() {
        EditCommunityFragmentArgs.fromBundle(requireArguments()).let {
            binding.etCommunityCategoryName.setText(it.communityData.communityCategory)
            communityData = it.communityData
        }

        viewModel.editCommunityRequestModel.value?.apply {
            communityName = communityData.communityName
            communityDescription = communityData.communityDescription
            status = communityData.status
        }

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
                    var imageFile: File? = null
                    if (data?.data != null) {
                        //Photo from gallery.
                        imageFile = FileUtils.getFileFromUri(context, data.data!!)
                    } else {
                        //Photo from camera.
                        imageFile = FileUtils.getImageFile(context)
                    }

                    if (imageFile != null) {
                        binding.ivProfilePic.loadCircularImageWithoutCache(imageFile.absolutePath)
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${imageFile.absolutePath}")
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
            viewModel.editCommunity(communityData.id)
        }
    }

    fun cancel() {
        findNavController().popBackStack()
    }
}