package com.ourstilt.homepage.data

import com.ourstilt.base.data.api.ApiService
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.data.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {
    fun getShopListData(forceRefresh: Boolean = false): Flow<NetworkResult<ShopPageData>> =
        safeApiCall { apiService.getShopListData(forceRefresh) }
}