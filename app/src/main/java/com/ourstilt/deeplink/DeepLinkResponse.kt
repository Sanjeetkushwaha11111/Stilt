package com.ourstilt.deeplink

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeepLinkResponse(
    val targetUrl: String,
    val type: String,
    val data: List<Map<String, String>>
) : Parcelable