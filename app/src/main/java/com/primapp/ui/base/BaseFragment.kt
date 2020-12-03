package com.primapp.ui.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.primapp.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.view.*
import javax.inject.Inject


abstract class BaseFragment<DB : ViewDataBinding> : DaggerFragment() {

    var isLoaded: Boolean = false

    open lateinit var binding: DB

    @Inject
    open lateinit var viewModelFactory: ViewModelProvider.Factory

    @LayoutRes
    abstract fun getLayoutRes(): Int

    var dialogLifeCycleEventObserver: LifecycleEventObserver? = null

    private lateinit var baseActivity: BaseActivity

    private fun init(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(inflater, container)
        super.onCreateView(inflater, container, savedInstanceState)

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BaseActivity) {
            this.baseActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        isLoaded = true
    }

    fun setToolbar(name: String, toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener {
            hideKeyBoard(toolbar)
            activity?.onBackPressed()
        }

        if (name.isNotEmpty())
            toolbar.tvTitle.setCompoundDrawables(null, null, null, null)
        toolbar.tvTitle.text = name
    }

    fun showLoading() {
        baseActivity.showLoading()
    }

    fun hideLoading() {
        baseActivity.hideLoading()
    }

    fun showCustomDialog(
        message: String,
        destinationId: Int? = null,
        requestCode: Int? = null,
        isHelperDialog: Boolean = false
    ) {
        val bundle = Bundle()
        bundle.putString("message", message)
        requestCode?.let {
            bundle.putInt("sourceId", it)
        }
        bundle.putBoolean("isHelperDialog", isHelperDialog)
        findNavController().navigate(R.id.popUpHelpMessage, bundle)

        // Add callback to dialog dismiss if the destination id is provided.
        if (dialogLifeCycleEventObserver == null && destinationId != null) {
            setDialogCallback(destinationId)
        }
    }

    private fun setDialogCallback(destinationId: Int) {
        val navBackStackEntry = findNavController().getBackStackEntry(destinationId)

        // Create observer and add it to the NavBackStackEntry's lifecycle
        dialogLifeCycleEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains("key")
            ) {
                /*val result =
                    navBackStackEntry.savedStateHandle.get<Boolean>("key")
                // Do something with the result
                Log.d("dialog_back", Gson().toJson(result))
                onDialogDismiss(navBackStackEntry.savedStateHandle.get<Any>("key"))

                */
                val result = navBackStackEntry.savedStateHandle.get<Int>("sourceId")
                onDialogDismiss(result)
            }
        }
        navBackStackEntry.lifecycle.addObserver(dialogLifeCycleEventObserver!!)
        Log.d("dialog_back", "Observer Added")

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(dialogLifeCycleEventObserver!!)
                dialogLifeCycleEventObserver = null
                Log.d("dialog_back", "Observer removed : ${dialogLifeCycleEventObserver}")
            }
        })
    }

    open fun onDialogDismiss(any: Any?) {
        Log.d("dialog_back", "dismissed base method called")
    }


    fun hideKeyBoard(input: View?) {
        input?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
        }
    }


    fun isPermissionGranted(permission: String): Boolean {
        val context = this.context

        if (context != null) {
            return ContextCompat
                .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        return false
    }


    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        var isGranted = true

        for (grantResult in grantResults) {
            isGranted = grantResult == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                break
            }
        }

        return isGranted
    }

    fun redirectUserToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }

}