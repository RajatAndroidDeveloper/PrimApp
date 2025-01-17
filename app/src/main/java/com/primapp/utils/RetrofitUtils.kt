package com.primapp.utils

import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.regex.Pattern

object RetrofitUtils {

    fun fileToRequestBody(file: File, fieldName: String = "file"): MultipartBody.Part {
        //val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        // MultipartBody.Part is used to send also the actual file addressFromUser
        return MultipartBody.Part.createFormData(fieldName, getOnlyStrings(file.name), requestFile)
    }

    fun bitmapToMultipartBody(bitmap: Bitmap, fileName: String, fieldName: String = "file"): MultipartBody.Part {
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
        val requestFile =
            byteStream.toByteArray()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, byteStream.toByteArray().size)
        return MultipartBody.Part.createFormData(fieldName, fileName, requestFile)
    }


    private fun getOnlyStrings(s: String): String {
        val pattern = Pattern.compile("[^a-z A-Z0-9~@#\$^*()_+=,.?:-]")  //Remove characters except these characters
        val matcher = pattern.matcher(s)
        return matcher.replaceAll("")
    }

    fun getRequestBody(field: String?): RequestBody? {
        if (field == null)
            return null
        else
            return field.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        //RequestBody.create(MediaType.parse("multipart/form-data"), field)
    }
}