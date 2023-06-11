package com.primapp.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.content.ContextCompat.startActivity
import com.primapp.R
import com.primapp.extensions.showNormalToast
import java.io.File


object DownloadUtils {
    fun download(context: Context, downloadManager: DownloadManager, url: String): Long {
        var mime: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        val fileName = URLUtil.guessFileName(url, null, null)
        Log.d("anshul_download", "Mime : ${mime} | Name : ${fileName}")
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
            .setDescription("Downloading...")
            /* .setDestinationInExternalFilesDir(
                 context,
                 Environment.DIRECTORY_DOWNLOADS, fileName
             )*/
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType(mime)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //Enqueue the download.The download will start automatically once the download manager is ready
        // to execute it and connectivity is available.
        showNormalToast(context, context.getString(R.string.download_started))
        return downloadManager.enqueue(request)
    }

    fun viewAllDownloads(): Intent {
        val intent = Intent()
        intent.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        return intent
    }

    fun openFile(fileName: String?, context: Context) {
        val install = Intent(Intent.ACTION_VIEW)
        install.setDataAndType(Uri.fromFile(File(fileName)), "MIME-TYPE")
        context.startActivity(install)
    }
}