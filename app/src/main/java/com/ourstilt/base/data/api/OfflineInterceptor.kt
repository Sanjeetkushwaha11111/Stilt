package com.ourstilt.base.data.api

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class OfflineInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!isNetworkAvailable(context)) {
            request = request.newBuilder().header(
                "Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24
            ) // Cache for 1 day
                .build()
        }
        return chain.proceed(request)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}
