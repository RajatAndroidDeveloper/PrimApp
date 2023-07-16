package com.primapp.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.util.Size
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.primapp.BuildConfig
import com.primapp.R
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


object FileUtils {
    const val IMAGE_REQUEST_CODE = 1
    const val VIDEO_REQUEST_CODE = 2
    const val GIF_REQUEST_CODE = 3
    const val FILE_REQUEST_CODE = 4
    const val PICK_ANY_FILE_REQUEST_CODE = 5
    const val FILE_PICK_TAG = "filePicker"

    const val IMAGE = "image"
    const val VIDEO = "video"
    const val GIF = "gif"

    fun getPickImageIntent(context: Context?): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getFileUri(context, IMAGE) != Uri.EMPTY) {
            var intentList: MutableList<Intent> = ArrayList()
            //Intent to show gallery option
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //Intent to add camera option to chooser
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, IMAGE))

            intentList = addIntentsToList(context, intentList, pickIntent)
            intentList = addIntentsToList(context, intentList, takePhotoIntent)

            if (intentList.size > 0) {
                chooserIntent = Intent.createChooser(
                    intentList.removeAt(intentList.size - 1),
                    context.getString(R.string.select_capture_image)
                )

                chooserIntent?.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray<Parcelable>()
                )
            }
        }

        return chooserIntent
    }

    fun getPickSingleImageIntent(context: Context?, pickerType: String): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getFileUri(context, IMAGE) != Uri.EMPTY) {

            if (pickerType == "Camera") {
                chooserIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, IMAGE))
            } else {
                chooserIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
        }

        return chooserIntent
    }

    fun getPickSingleVideoIntent(context: Context?, pickerType: String): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getFileUri(context, VIDEO) != Uri.EMPTY) {
            val videoSize: Long = 17 * 1024 * 1024

            if (pickerType == "Camera") {
                chooserIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                chooserIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                chooserIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, videoSize)
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, VIDEO))
            } else {
                chooserIntent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            }
        }

        return chooserIntent
    }

    fun getPickVideoIntent(context: Context?): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getFileUri(context, VIDEO) != Uri.EMPTY) {
            var intentList: MutableList<Intent> = ArrayList()
            //Intent to show gallery option
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            val videoSize: Long = 17 * 1024 * 1024
            //Intent to add camera option to chooser
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, videoSize)
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, VIDEO))

            intentList = addIntentsToList(context, intentList, pickIntent)
            intentList = addIntentsToList(context, intentList, takeVideoIntent)

            if (intentList.size > 0) {
                chooserIntent = Intent.createChooser(
                    intentList.removeAt(intentList.size - 1),
                    context.getString(R.string.select_capture_video)
                )

                chooserIntent?.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray<Parcelable>()
                )
            }
        }

        return chooserIntent
    }


    fun getGifIntent(context: Context?): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getFileUri(context, GIF) != Uri.EMPTY) {
            var intentList: MutableList<Intent> = ArrayList()

            val mimeTypes = arrayOf("images/gif")
            //Intent to show gallery option
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.setType("image/*")
            pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, GIF));

            intentList = addIntentsToList(context, intentList, pickIntent)

            if (intentList.size > 0) {
                chooserIntent = Intent.createChooser(
                    intentList.removeAt(intentList.size - 1),
                    context.getString(R.string.select_capture_video)
                )

                chooserIntent?.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray<Parcelable>()
                )
            }
        }

        return chooserIntent
    }

    private fun getFileUri(context: Context?, type: String): Uri {
        val file = getFile(context, type)

        if (context != null && file != null) {
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + context.getString(R.string.file_provider_name),
                file
            )
        }
        return Uri.EMPTY
    }

    fun getFile(context: Context?, type: String): File? {
        if (context != null) {
            val pathname = "${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)}"

            val folder = File(pathname)
            folder.mkdirs()

            var fileName = "image_temp.jpg"

            when (type) {
                IMAGE -> {
                    fileName = "image_temp.jpg"
                }

                VIDEO -> {
                    fileName = "video_temp.mov"
                }

                GIF -> {
                    fileName = "gif_temp.gif"
                }
            }

            val file = File(folder, fileName)
            Log.d(FILE_PICK_TAG, "VideoFile: $file")
            file.createNewFile()
            return file
        }

        return null
    }

    private fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }

        return list
    }

    /*
    *   Get Image file from URI (i.e. Call when you get image from gallery)
    * */

    fun getFileFromUri(context: Context?, uri: Uri, type: String): File? {
        val imageFile = getFile(context, type)

        if (context != null && imageFile != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream.use { input ->
                val outputStream = FileOutputStream(imageFile)
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024) // buffer size
                    while (true) {
                        val byteCount = input?.read(buffer)

                        if (byteCount != null) {
                            if (byteCount < 0) break
                            output.write(buffer, 0, byteCount)
                        }
                    }
                    output.flush()
                    return imageFile
                }
            }
        }
        return null
    }


    fun getPathFromURI(context: Context, uri: Uri): String {
        var realPath = String()
        uri.path?.let { path ->

            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = context.contentResolver.query(
                    databaseUri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        return realPath
    }


    fun compressImage(imageUrl: String) {
        val compressionRatio = 60 //1 == originalImage, 2 = 50% compression, 4=25% compress

        val file = File(imageUrl)
        val fileSize = (file.length() / 1024) / 1024
        try {
            if (fileSize >= 1) {
                val oldExif = ExifInterface(imageUrl)
                val exifOrientation: String? = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)

                Log.d(FILE_PICK_TAG, "Compressing File....")
                val bitmap = BitmapFactory.decodeFile(file.path)
                Log.d(
                    FILE_PICK_TAG,
                    "Original size : $fileSize Resolution : ${bitmap.width}X${bitmap.height}"
                )
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    compressionRatio,
                    FileOutputStream(file)
                )

                if (exifOrientation != null) {
                    val newExif = ExifInterface(imageUrl)
                    newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
                    newExif.saveAttributes()
                }

                val compressedBitmap = BitmapFactory.decodeFile(file.path)
                val compressedFileSize = (file.length() / 1024) / 1024
                Log.d(
                    FILE_PICK_TAG,
                    "Compressed size : $compressedFileSize Resolution : ${compressedBitmap.width}X${compressedBitmap.height}"
                )

            }
        } catch (t: Throwable) {
            Log.e(FILE_PICK_TAG, "Error compressing file.$t")
            t.printStackTrace()
        }
    }

    fun getBitmapThumbnailForVideo(context: Context, file: File): Bitmap {
        val bitmap: Bitmap?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap = ThumbnailUtils.createVideoThumbnail(file, Size(500, 500), null)
        } else {
            bitmap = ThumbnailUtils.createVideoThumbnail(
                file.absolutePath,
                MediaStore.Video.Thumbnails.MINI_KIND
            )
        }

        if (bitmap == null) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.logo);
        } else {
            return bitmap
        }
    }

    //For share post as Image

    fun getBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap =
            Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable: Drawable? = view.getBackground()
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    fun getURIFromBitmap(context: Context, bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = getFile(context, IMAGE)

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
            Log.d("anshul_uri", bmpUri.toString())
            bmpUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + context.getString(R.string.file_provider_name),
                file!!
            )
            Log.d("anshul_uri2", bmpUri.toString())

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    //Request Doc
    fun getDocumentFileIntent(): Intent {
        val mimeTypes = arrayOf(
            MIMETypes.APPLICATION_DOC,
            MIMETypes.APPLICATION_DOCX,  // .doc & .docx
            MIMETypes.APPLICATION_PPT,
            MIMETypes.APPLICATION_PPTX,  // .ppt & .pptx
            MIMETypes.APPLICATION_XLS,
            MIMETypes.APPLICATION_XLSX,  // .xls & .xlsx
            "text/*", // includes text/plain and text/csv
            MIMETypes.APPLICATION_PDF,
        )
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.type = "file/*"
        return intent
    }

    //Request any file
    fun requestAnyFile(context: Context): Intent {
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, IMAGE))
        val videoSize: Long = 10 * 1024 * 1024
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, videoSize)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(context, VIDEO))

        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "*/*"

        val intentArray = arrayOf(takePictureIntent, takeVideoIntent)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose a media file")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

        return chooserIntent
    }

    fun getFileFromUri(context: Context, selectedFileUri: Uri): File? {
        val contentResolver = context.contentResolver
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedFileUri, "r", null) ?: return null

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, contentResolver.getFileName(selectedFileUri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        return file
    }

    fun getFileInfo(
        context: Context,
        uri: Uri
    ): Hashtable<String, Any?>? {
        try {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                val mime = context.contentResolver.getType(uri)
                if (cursor != null) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val value =
                        Hashtable<String, Any?>()
                    if (cursor.moveToFirst()) {
                        var name = cursor.getString(nameIndex)
                        val size = cursor.getLong(sizeIndex).toInt()
                        if (TextUtils.isEmpty(name)) {
                            name =
                                "Temp_" + uri.hashCode() + "." + extractExtension(context, uri)
                        }
                        val file = File(context.cacheDir, name)
                        val inputPFD = context.contentResolver.openFileDescriptor(uri, "r")
                        var fd: FileDescriptor? = null
                        if (inputPFD != null) {
                            fd = inputPFD.fileDescriptor
                        }
                        val inputStream = FileInputStream(fd)
                        val outputStream = FileOutputStream(file)
                        var read: Int
                        val bytes = ByteArray(1024)
                        while (inputStream.read(bytes).also { read = it } != -1) {
                            outputStream.write(bytes, 0, read)
                        }
                        value["path"] = file.absolutePath
                        value["size"] = size
                        value["mime"] = mime
                        value["name"] = name
                    }
                    return value
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(e.localizedMessage, "File not found.")
            return null
        }
        return null
    }

    fun getFileInfo(
        context: Context,
        file: File
    ): Hashtable<String, Any?>? {
        try {
            val value = Hashtable<String, Any?>()
            var mime: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
            if (extension != null) {
                mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            val size = Integer.parseInt((file.length()).toString());
            value["path"] = file.absolutePath
            value["size"] = size
            value["mime"] = mime
            value["name"] = file.name

            return value
        } catch (e: java.lang.Exception) {
            Log.e(FILE_PICK_TAG, "File not found.")
            return null
        }
        return null
    }

    private fun extractExtension(context: Context, uri: Uri): String? {
        val extension: String = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            extractExtension(context.contentResolver.getType(uri).toString()) ?: ""
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }


    private fun extractExtension(mimeType: String): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(mimeType)
    }

    @JvmStatic
    fun getIconUsingFileURl(url: String?): Int {
        var mime: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        Log.d("anshul_mime", "Mime : ${mime}")
        return getIconResourceBasedOnMimeType(mime)
    }

    @JvmStatic
    fun getIconResourceBasedOnMimeType(mimeType: String?): Int {
        return when (mimeType) {
            MIMETypes.TEXT_PLAIN -> R.drawable.ic_text
            MIMETypes.APPLICATION_PDF -> R.drawable.ic_pdf
            MIMETypes.APPLICATION_DOC, MIMETypes.APPLICATION_DOCX -> R.drawable.ic_word
            MIMETypes.APPLICATION_XLS, MIMETypes.APPLICATION_XLSX -> R.drawable.ic_excel
            MIMETypes.APPLICATION_PPT, MIMETypes.APPLICATION_PPTX -> R.drawable.ic_powerpoint
            MIMETypes.TEXT_CSV, MIMETypes.TEXT_CSV_FULL -> R.drawable.ic_csv
            else -> R.drawable.ic_file_icon
        }
    }
}

object MIMETypes {
    const val TEXT_PLAIN = "text/plain"
    const val TEXT_CSV = "text/csv"
    const val TEXT_CSV_FULL = "text/comma-separated-values"
    const val APPLICATION_PDF = "application/pdf"
    const val APPLICATION_FLASH = "application/x-shockwave-flash"
    const val APPLICATION_OCTET_STREAM = "application/octet-stream"
    const val APPLICATION_DOC = "application/msword"
    const val APPLICATION_DOCX =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    const val APPLICATION_XLS = "application/vnd.ms-excel"
    const val APPLICATION_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val APPLICATION_PPT = "application/vnd.ms-powerpoint"
    const val APPLICATION_PPTX =
        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
}