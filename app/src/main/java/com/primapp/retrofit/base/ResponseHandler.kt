package com.primapp.retrofit.base


import android.util.Log
import com.google.gson.stream.MalformedJsonException
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

open class ResponseHandler @Inject constructor() {

    fun <T : Any> handleResponse(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> sendLoading(data: T?): Resource<T> {
        return Resource.loading(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        Log.e("API_ERROR", "Reason : ${e.message}")
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e), null)
            is MalformedJsonException -> Resource.error(getErrorMessage(46456), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(25345), null)
            is IOException -> Resource.error(getErrorMessage(403), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(e: HttpException): String {
        return getErrorMsg(e.response()?.errorBody()!!).error
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            //   ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            403 -> "Internet Connection Not Found"
            else -> "Something went wrong"
        }
    }

    fun getErrorMsg(responseBody: ResponseBody): BaseError {

        try {
            val jsonObject = JSONObject(responseBody.string())

            return BaseError(101, jsonObject.getString("error"))

        } catch (e: Exception) {
            return BaseError(101, e.message!!)
        }

    }

}