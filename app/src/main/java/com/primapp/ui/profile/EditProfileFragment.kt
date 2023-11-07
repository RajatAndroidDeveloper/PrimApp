package com.primapp.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.primapp.R
import com.primapp.cache.UserCache
import com.primapp.chat.ConnectionManager
import com.primapp.constants.ReferenceEntityTypes
import com.primapp.databinding.FragmentEditProfileBinding
import com.primapp.extensions.loadCircularImage
import com.primapp.extensions.loadCircularImageWithName
import com.primapp.extensions.loadCircularImageWithoutCache
import com.primapp.extensions.showError
import com.primapp.retrofit.base.Status
import com.primapp.ui.base.BaseFragment
import com.primapp.ui.dashboard.DashboardActivity
import com.primapp.ui.initial.AutocompleteListArrayAdapter
import com.primapp.utils.AwsHelper
import com.primapp.utils.DialogUtils
import com.primapp.utils.FileUtils
import com.primapp.utils.RetrofitUtils
import com.primapp.viewmodels.EditProfileViewModel
import com.sendbird.android.SendbirdChat
import com.sendbird.android.params.UserUpdateParams
import kotlinx.android.synthetic.main.item_amend_request_layout.ivUserImage
import kotlinx.android.synthetic.main.toolbar_inner_back.*
import java.io.File

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    var imageFile: File? = null

    val userData by lazy { UserCache.getUser(requireContext()) }

    val viewModel by viewModels<EditProfileViewModel> { viewModelFactory }

    val genderAdapter by lazy {
        AutocompleteListArrayAdapter(
            requireContext(),
            R.layout.item_simple_text
        )
    }
    val countryAdapter by lazy {
        AutocompleteListArrayAdapter(
            requireContext(),
            R.layout.item_simple_text
        )
    }

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
            data?.gender = gender
            data?.countryIsoCode = countryIsoCode
            binding.mAutoCompleteGender.setText(genderValue)
            binding.mAutoCompleteCountry.setText(country)
            binding.etUserName.setText(userName)
            viewModel.editProfileRequestModel.value = data
            //Show image file
            if (!isInappropriate)
                binding.ivProfilePic.loadCircularImage(requireContext(), userImage)
            else
                binding.ivProfilePic.loadCircularImageWithName(data?.firstName + " " + data?.lastName, "")
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
                                val usaItem = this.find { it.itemValue.equals("US") }
                                usaItem?.let {
                                    val index: Int = this.indexOf(it)
                                    this.removeAt(index)
                                    this.add(0, it)
                                }
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
                    DialogUtils.showCloseDialog(
                        requireActivity(),
                        R.string.changes_saved_successfully
                    ) {
                        it.data?.content?.let { user ->
                            UserCache.saveUser(requireContext(), user)
                            (activity as? DashboardActivity)?.updateCurrentUserInfo(
                                "${user.firstName} ${user.lastName}",
                                user.userImage
                            )
                        }
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
                            viewModel.editProfileRequestModel.value?.userImageFile = it.fields.key
                            viewModel.uploadAWS(
                                it.url,
                                it.fields.key ?: "",
                                it.fields.aWSAccessKeyId ?: "",
                                it.fields.xAmzSecurityToken ?: "",
                                it.fields.policy ?: "",
                                it.fields.signature ?: "",
                                it.fields.xAmzAlgorithm ?: "",
                                it.fields.xAmzCredential ?: "",
                                it.fields.xAmzDate ?: "",
                                it.fields.xAmzSignature ?: "",
                                RetrofitUtils.fileToRequestBody(
                                    File(imageFile!!.absolutePath),
                                    "file"
                                )
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
                        viewModel.editProfile(userData!!.id)
                    }
                }
            }
        })
    }

    fun save() {
        binding.mAutoCompleteGender.clearFocus()
        binding.mAutoCompleteCountry.clearFocus()
        if (viewModel.validateData()) {
            if (imageFile != null) {
                viewModel.generatePresignedUrl(
                    "user-id-${UserCache.getUserId(requireContext())}/"+requireActivity().getString(R.string.profile_image_folder) + "" + AwsHelper.getObjectName(
                        AwsHelper.AWS_OBJECT_TYPE.USER,
                        userData!!.id,
                        imageFile!!.extension
                    )
                )
                //Upload image file to Sendbird parallely
                updateCurrentUserProfileImage()
            } else {
                viewModel.editProfile(userData!!.id)
            }
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

        binding.mAutoCompleteCountry.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                val itemData = countryAdapter.getItem(pos)
                val data = viewModel.editProfileRequestModel.value
                data?.countryIsoCode = itemData.itemValue
                viewModel.editProfileRequestModel.value = data
            }

        binding.mAutoCompleteGender.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                val itemData = genderAdapter.getItem(pos)
                val data = viewModel.editProfileRequestModel.value
                data?.gender = itemData.itemId
                viewModel.editProfileRequestModel.value = data
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

    private fun updateCurrentUserProfileImage() {
        val nickname =
            if (SendbirdChat.currentUser != null) SendbirdChat.currentUser?.nickname else ""

        val params = UserUpdateParams().apply {
            this.nickname = nickname
            this.profileImageFile = imageFile
        }

        SendbirdChat.updateCurrentUserInfo(params) {
            if (it != null) {
                Log.d(ConnectionManager.TAG, "Failed to update Profile Image to sendbird")
                return@updateCurrentUserInfo
            }
            Log.d(ConnectionManager.TAG, "Updated the Profile Image")
        }
    }
}