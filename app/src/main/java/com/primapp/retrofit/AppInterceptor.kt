package com.primapp.retrofit

import android.content.Context
import android.util.Log
import com.primapp.cache.UserCache
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AppInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val accessToken = UserCache.getAccessToken(context = context)

        val customAnnotations: List<String> = request.headers("@")

        val requestBuilder: Request.Builder = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        if (accessToken.isNotEmpty() && customAnnotations.isEmpty()) {
            requestBuilder.addHeader("Authorization", "JWT $accessToken")
            Log.d("OkHttp", "Authorization JWT $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}

