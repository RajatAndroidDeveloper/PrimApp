package com.primapp.ui.base

import android.os.Build
import android.os.Bundle
import android.view.Window
import com.primapp.R
import com.primapp.databinding.LayoutImageViewBinding
import com.primapp.extensions.loadImageWithFitCenter

class ImageViewDialog : BaseDialogFragment<LayoutImageViewBinding>() {

    override fun getLayoutRes(): Int = R.layout.layout_image_view

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            dialog?.window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onActivityCreated(savedInstanceState)

        setData()
    }

    private fun setData() {
        isCancelable = true

        val url = ImageViewDialogArgs.fromBundle(requireArguments()).url

        binding.imageView.loadImageWithFitCenter(requireContext(), url)

        binding.rlMain.setOnClickListener {
            dismiss()
        }
    }
}