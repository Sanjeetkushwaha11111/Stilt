package com.ourstilt.base.data.api

import com.ourstilt.deeplink.DeepLinkResponse
import com.ourstilt.userCustomMenu.data.CustomMenuModel
import com.ourstilt.userCustomMenu.data.CustomMenus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    companion object {
        const val BASE_URL = "http://192.168.1.24:4000/"
    }

    @GET("getMenuData")
    suspend fun getMenuPageData(@Header("Force-Refresh") forceRefresh: Boolean = false): Response<ApiResponse<CustomMenuModel>>

    @POST("data")
    suspend fun placeFoodOrder(
        @Header("Force-Refresh") forceRefresh: Boolean = false,
        @Body customMenus: CustomMenus
    ): Response<ApiResponse<String>>

    @GET("deep_link_data")
    suspend fun fetchDeepLinkData(
        @Header("Force-Refresh") forceRefresh: Boolean = true,
        @Query("url") deepLinkUrl: String
    ): Response<ApiResponse<DeepLinkResponse>>

    @POST("get_assistant_requested_data")
    suspend fun processVoiceCommand(
        @Body request: VoiceRequest
    ): VoiceResponse

    @POST("place_order")
    suspend fun placeOrder(
        @Body orderRequest: OrderRequest
    ): OrderResponse
}