package com.primapp.ui.base

import android.app.DownloadManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import com.primapp.R
import com.primapp.databinding.LayoutImageViewBinding
import com.primapp.extensions.loadCircularImageWithName
import com.primapp.extensions.loadImageWithFitCenter
import com.primapp.utils.DownloadUtils
import javax.inject.Inject

class ImageViewDialog : BaseDialogFragment<LayoutImageViewBinding>() {

    @Inject
    lateinit var downloadManager: DownloadManager

    override fun getLayoutRes(): Int = R.layout.layout_image_view

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            dialog?.window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        isCancelable = true

        val url = ImageViewDialogArgs.fromBundle(requireArguments()).url

        if(ImageViewDialogArgs.fromBundle(requireArguments()).isInAppropriate)
            binding.imageView.loadCircularImageWithName(ImageViewDialogArgs.fromBundle(requireArguments()).fullName, "")
        else
            binding.imageView.loadImageWithFitCenter(requireContext(), url)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.fabDownload.setOnClickListener {
            url?.let { it1 ->
                DownloadUtils.download(requireContext(), downloadManager, it1)
            }
        }
    }
}