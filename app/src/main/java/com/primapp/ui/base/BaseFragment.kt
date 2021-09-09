package com.primapp.ui.base

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.primapp.BuildConfig
import com.primapp.R
import com.primapp.extensions.showNormalToast
import com.primapp.utils.AnalyticsManager
import com.primapp.utils.FileUtils
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.toolbar_inner_back.view.*
import javax.inject.Inject

abstract class BaseFragment<DB : ViewDataBinding> : DaggerFragment() {

    var isLoaded: Boolean = false

    open lateinit var binding: DB

    @Inject
    open lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analyticsManager: AnalyticsManager

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
            toolbar.tvTitle.text = name
    }

    fun showLoading() {
        baseActivity.showLoading()
    }

    fun hideLoading() {
        baseActivity.hideLoading()
    }

    fun hideKeyBoard(input: View?) {
        input?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showKeyBoard(input: View?) {
        input?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
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

    fun copyTextToClipboard(textToCopy: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", textToCopy)
        clipboard.setPrimaryClip(clip)
        showNormalToast(requireContext(), getString(R.string.copied_to_clipboard))
    }

    //Share Post as Image
    fun sharePostAsImage(view: View, authorName: String, communityName: String) {
        val bitmap = FileUtils.getBitmapFromView(view)
        val textToSend =
            "Check this post by $authorName in $communityName only on virtual mentoring platform ${
                context?.getString(
                    R.string.app_name
                )
            }. Download now : https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, FileUtils.getURIFromBitmap(requireContext(), bitmap))
        intent.putExtra(Intent.EXTRA_TEXT, textToSend)
        startActivity(Intent.createChooser(intent, "Share Post"))
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }

}