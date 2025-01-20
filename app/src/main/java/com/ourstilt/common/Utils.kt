package com.ourstilt.common

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AnimRes
import java.io.InputStreamReader
import java.nio.charset.Charset

internal object Utils {
    @SuppressLint("ResourceType")
    fun loadInterpolator(
        context: Context, @AnimRes resId: Int,
        defaultInterpolator: Interpolator
    ): Interpolator {
        if (resId > 0) {
            return AnimationUtils.loadInterpolator(context, resId)
        }

        return defaultInterpolator
    }

    @SuppressLint("ObsoleteSdkInt")
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(nw) ?: return false

            return networkCapabilities.run {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
    fun readFileFromAssets(context: Context, filename: String): String {
        val assetManager = context.assets
        val inputStream = assetManager.open(filename)
        val reader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
        val jsonString = reader.readText()
        reader.close()
        return jsonString
    }
}