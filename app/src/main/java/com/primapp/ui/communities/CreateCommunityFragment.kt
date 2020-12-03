package com.primapp.ui.communities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.primapp.R
import com.primapp.binding.loadCircularImageFromUrl
import com.primapp.databinding.FragmentCreateCommunityBinding
import com.primapp.extensions.loadCircularImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.ui.base.BaseFragment
import com.primapp.utils.FileUtils
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File


class CreateCommunityFragment : BaseFragment<FragmentCreateCommunityBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_create_community

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setToolbar(getString(R.string.create_community), toolbar)
        setData()
    }

    private fun setData() {
        binding.frag = this
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
                        showCustomDialog(
                            getString(R.string.error_camera_permission),
                            isHelperDialog =
                            true
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

}