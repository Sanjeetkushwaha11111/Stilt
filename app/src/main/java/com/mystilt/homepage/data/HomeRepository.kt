package com.mystilt.homepage.data

import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {

    fun getShopListData(forceRefresh: Boolean = false): Flow<NetworkResult<ShopPageData>> =
        safeApiCall { apiService.getShopListData(forceRefresh) }

    fun getHomeActivityData(forceRefresh: Boolean): Flow<NetworkResult<HomeDataModel>> =
        safeApiCall { apiService.getHomeActivityData(forceRefresh) }
}