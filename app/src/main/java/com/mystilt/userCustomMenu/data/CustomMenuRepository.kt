package com.mystilt.userCustomMenu.data

import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomMenuRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {
    fun getMenuPageData(forceRefresh: Boolean = false): Flow<NetworkResult<CustomMenuModel>> =
        safeApiCall { apiService.getMenuPageData(forceRefresh) }

    fun placeFoodOrder(customMenus: CustomMenus, forceRefresh: Boolean = false): Flow<NetworkResult<String>> =
        safeApiCall { apiService.placeFoodOrder(forceRefresh, customMenus) }

}