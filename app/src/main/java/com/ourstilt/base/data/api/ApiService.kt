package com.ourstilt.base.data.api

import com.ourstilt.userCustomMenu.data.CustomMenuModel
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    companion object {
        const val BASE_URL = "http://192.168.1.38:3001/"
    }

    @GET("data")
    suspend fun getMenuPageData(): Response<ApiResponse<CustomMenuModel>>
}