package com.ourstilt.base.data.api

import com.ourstilt.deeplink.DeepLinkResponse
import com.ourstilt.homepage.data.HomeDataModel
import com.ourstilt.homepage.data.ShopPageData
import com.ourstilt.search.data.SearchPageData
import com.ourstilt.search.data.TrendingPageData
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
        const val BASE_URL = "http://192.168.0.152:4000/"
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

    @GET("shop-page-data")
    suspend fun getShopListData(
        @Header("Force-Refresh") forceRefresh: Boolean = true,
    ): Response<ApiResponse<ShopPageData>>

    @GET("home-activity-data")
    suspend fun getHomeActivityData(
        @Header("Force-Refresh") forceRefresh: Boolean = true
    ): Response<ApiResponse<HomeDataModel>>

    @GET("search-page-data")
    suspend fun getSearchPageData(
        @Header("Force-Refresh") forceRefresh: Boolean = true
    ): Response<ApiResponse<SearchPageData>>

    @GET("trending-page-data")
    suspend fun getTrendingPageData(
        @Header("Force-Refresh") forceRefresh: Boolean = true
    ): Response<ApiResponse<TrendingPageData>>
}