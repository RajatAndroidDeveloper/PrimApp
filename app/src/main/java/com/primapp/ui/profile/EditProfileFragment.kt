package com.primapp.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.constants.ReferenceEntityTypes
import com.primapp.databinding.FragmentEditProfileBinding
import com.primapp.extensions.loadCircularImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.viewmodels.EditProfileViewModel
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    val userData by lazy { UserCache.getUser(requireContext()) }

    val viewModel by viewModels<EditProfileViewModel> { viewModelFactory }

    val genderAdapter by lazy { AutocompleteListArrayAdapter(requireContext(), R.layout.item_simple_text) }
    val countryAdapter by lazy { AutocompleteListArrayAdapter(requireContext(), R.layout.item_simple_text) }

    override fun getLayoutRes(): Int = R.layout.fragment_edit_profile

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbar(getString(R.string.edit_profile), toolbar)
        setData()
        setAdapter()
        setObserver()
        getReferenceData()
    }

    private fun setData() {
        binding.frag = this
        binding.viewModel = viewModel

        userData?.apply {
            val data = viewModel.editProfileRequestModel.value
            data?.firstName = firstName
            data?.lastName = lastName
            data?.profileSummary = profileSummary
            data?.userImageFile = userImage
            data?.gender = gender
            data?.countryIsoCode = countryIsoCode
            binding.mAutoCompleteGender.setText(genderValue)
            binding.mAutoCompleteCountry.setText(country)
            binding.etUserName.setText(userName)
            viewModel.editProfileRequestModel.value = data
        }
    }

    private fun setObserver() {
        viewModel.referenceLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.SUCCESS -> {
                    when (it.data?.content?.entity) {
                        ReferenceEntityTypes.GENDER_LIST -> {
                            it.data.content.referenceItemsList?.apply {
                                genderAdapter.addAll(this)
                            }
                        }
                        ReferenceEntityTypes.COUNTRY_LIST -> {
                            it.data.content.referenceItemsList?.apply {
                                countryAdapter.addAll(this)
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    it.message?.apply {
                        showError(requireContext(), this)
                    }
                    findNavController().popBackStack()
                }
                Status.LOADING -> {
                    showLoading()
                }
            }
        })

        viewModel.editProfileLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it.status) {
                Status.SUCCESS -> {
                    DialogUtils.showCloseDialog(requireActivity(), R.string.changes_saved_successfully) {
                        it.data?.content?.let { user -> UserCache.saveUser(requireContext(), user) }
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
        })
    }

    fun save() {
        binding.mAutoCompleteGender.clearFocus()
        binding.mAutoCompleteCountry.clearFocus()
        if (viewModel.validateData()) {
            viewModel.editProfile(userData!!.id)
        }
    }

    private fun getReferenceData() {
        viewModel.getReferenceData(ReferenceEntityTypes.GENDER_LIST)
        viewModel.getReferenceData(ReferenceEntityTypes.COUNTRY_LIST)
    }

    private fun setAdapter() {
        context?.apply {


            binding.mAutoCompleteGender.setAdapter(genderAdapter)

            binding.mAutoCompleteGender.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = genderAdapter.contains(p0.toString())
                    val data = viewModel.editProfileRequestModel.value
                    if (isDataValid) {
                        data?.gender = genderAdapter.getItemId(p0.toString())
                    } else {
                        data?.gender = null
                    }

                    viewModel.editProfileRequestModel.value = data

                    return isDataValid
                }
            }



            binding.mAutoCompleteCountry.setAdapter(countryAdapter)

            binding.mAutoCompleteCountry.validator = object : AutoCompleteTextView.Validator {
                override fun fixText(p0: CharSequence?): CharSequence {
                    return ""
                }

                override fun isValid(p0: CharSequence?): Boolean {
                    val isDataValid = countryAdapter.contains(p0.toString())
                    val data = viewModel.editProfileRequestModel.value
                    if (isDataValid) {
                        data?.countryIsoCode = countryAdapter.getItemKey(p0.toString())
                    } else {
                        data?.countryIsoCode = null
                    }
                    viewModel.editProfileRequestModel.value = data
                    return isDataValid
                }
            }

        }
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
}