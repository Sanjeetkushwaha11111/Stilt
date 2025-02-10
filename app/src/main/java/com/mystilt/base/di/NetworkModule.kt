package com.mystilt.base.di

import android.content.Context
import com.mystilt.BuildConfig
import com.mystilt.base.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private fun provideCacheLoggingInterceptor(forceRefresh: Boolean = false) =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val request = when (forceRefresh) {
                true -> originalRequest.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

                false -> originalRequest
            }
            try {
                val response = chain.proceed(request)
                val cacheStatus = when {
                    response.networkResponse == null && response.cacheResponse != null -> "CACHE HIT"
                    response.networkResponse != null && response.cacheResponse != null -> "CONDITIONAL CACHE HIT"
                    response.networkResponse != null && response.cacheResponse == null -> "CACHE MISS"
                    else -> "UNKNOWN CACHE STATUS Response:>>>>>"
                }
                Timber.e("CACHE STATUS Response:>>>>> $cacheStatus")
           //     Timber.e("RESPONSE HEADERS Response:>>>>> ${response.headers}")

                response
            } catch (e: Exception) {
                Timber.e("NETWORK ERROR Response:>>>>> ${e.message}")
                if (!forceRefresh) {
                    Timber.e("ATTEMPTING STALE CACHE Response:>>>>>")
                    val newRequest = request.newBuilder()
                        .cacheControl(
                            CacheControl.Builder()
                                .maxStale(7, TimeUnit.DAYS)
                                .build()
                        )
                        .build()

                    val cachedResponse = chain.proceed(newRequest)
                    Timber.e("Response:>>>>> ${cachedResponse.cacheResponse != null}")
                    cachedResponse
                } else {
                    throw e
                }
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = 10L * 1024L * 1024L // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)
        val logInterceptor = HttpLoggingInterceptor { message ->
            if (BuildConfig.DEBUG) {
                Timber.e("Response:>>>>>  $message")
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain.request()
                val forceRefresh = request.header("Force-Refresh")?.toBoolean() ?: false
                provideCacheLoggingInterceptor(forceRefresh).intercept(chain)
            }
            .addInterceptor(logInterceptor)
            .addNetworkInterceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=3600, must-revalidate")
                    .build()
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}