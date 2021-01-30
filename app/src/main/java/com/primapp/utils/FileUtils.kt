package com.primapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.content.FileProvider
import com.primapp.BuildConfig
import com.primapp.R
import java.io.File
import java.io.FileOutputStream


object FileUtils {
    const val IMAGE_REQUEST_CODE = 1
    const val VIDEO_REQUEST_CODE = 2
    const val GIF_REQUEST_CODE = 3
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
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
            if (fileSize >= 2) {
                val oldExif = ExifInterface(imageUrl)
                val exifOrientation: String? = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)

                Log.d(FILE_PICK_TAG, "Compressing File....")
                val bitmap = BitmapFactory.decodeFile(file.path)
                Log.d(FILE_PICK_TAG, "Original size : $fileSize Resolution : ${bitmap.width}X${bitmap.height}")
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressionRatio, FileOutputStream(file))

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

}