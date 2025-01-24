package com.ourstilt.deeplink

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeepLinkResponse(
    val targetUrl: String,
    val type: String,
    val deepLinkData: List<DeepLinkData>
) : Parcelable

@Parcelize
data class DeepLinkData(val key: String, val value: String) : Parcelable