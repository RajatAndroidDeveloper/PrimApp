package com.primapp.di.modules

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import com.primapp.di.CoroutineScopeIO
import com.primapp.di.PrimAPI
import com.primapp.di.viewModels.ViewModelModule
import com.primapp.retrofit.ApiConstant
import com.primapp.retrofit.ApiService
import com.primapp.retrofit.AppInterceptor
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.firebase.analytics.FirebaseAnalytics
import com.primapp.utils.AnalyticsManager


@Module(includes = [ViewModelModule::class, NetworkModule::class])
class AppModule {

    @CoroutineScopeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

    @PrimAPI
    @Provides
    fun providePrivateOkHttpClient(upstreamClient: OkHttpClient, context: Context): OkHttpClient {
        return upstreamClient.newBuilder()
            .addInterceptor(AppInterceptor(context)).build()
    }

    @Singleton
    @Provides
    fun provideLegoService(
        @PrimAPI okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ) = provideService(okhttpClient, converterFactory, ApiService::class.java)

    private fun <T> provideService(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory,
        clazz: Class<T>
    ): T {
        return createRetrofit(okhttpClient, converterFactory).create(clazz)
    }

    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(okhttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideDownloadManager(context: Context): DownloadManager {
        return context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    @Provides
    fun providesFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesAnalyticsManager(firebaseAnalytics: FirebaseAnalytics): AnalyticsManager {
        return AnalyticsManager(firebaseAnalytics)
    }
}