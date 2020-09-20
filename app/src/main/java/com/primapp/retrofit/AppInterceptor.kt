package com.primapp.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppInterceptor(private val accessToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "key $accessToken").build()
        return chain.proceed(request)
    }
}

