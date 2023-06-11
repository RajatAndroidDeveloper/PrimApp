package com.primapp.ui.chat

import android.app.DownloadManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.primapp.R
import com.primapp.databinding.LayoutPdfViewBinding
import com.primapp.ui.base.BaseDialogFragment
import com.primapp.ui.base.ImageViewDialogArgs
import com.primapp.utils.getFileName
import javax.inject.Inject


class PDFViewerDialog : BaseDialogFragment<LayoutPdfViewBinding>(),
    OnPageErrorListener {

    @Inject
    lateinit var downloadManager: DownloadManager

    override fun getLayoutRes(): Int = R.layout.layout_pdf_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        isCancelable = true

        val url = ImageViewDialogArgs.fromBundle(requireArguments()).url
        binding.imageView.fromUri(Uri.parse(url))
            .enableAnnotationRendering(true)
            .spacing(10) // in dp
            .onPageError(this)
            .load();


        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.ivSend.setOnClickListener {
            dismiss()
        }

    }

    override fun onPageError(page: Int, t: Throwable?) {
        Log.e("errrorrrr",t!!.message!!+"Asas")
    }

}