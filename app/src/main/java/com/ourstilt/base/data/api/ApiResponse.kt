package com.ourstilt.base.data.api

import com.google.gson.annotations.SerializedName


data class ApiResponse<T>(
    val status: Boolean = true,
    val message: String? = null,
    val errorCode: Int? = null,
    @SerializedName("data") val data: T? = null
)