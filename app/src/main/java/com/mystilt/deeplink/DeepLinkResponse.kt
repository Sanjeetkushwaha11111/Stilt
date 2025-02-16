package com.mystilt.deeplink

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeepLinkResponse(
    val targetUrl: String,
    val type: String,
    val deepLinkData: List<DeepLinkData>,
) : Parcelable

@Parcelize
data class DeepLinkData(val key: String, val value: String) : Parcelable