package com.mystilt.shops.data

import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.BaseRepository
import com.mystilt.shops.ui.ShopPageModel
import com.mystilt.userCustomMenu.data.CustomMenuModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {
    fun getShopPageData(forceRefresh: Boolean = false): Flow<NetworkResult<ShopPageModel>> =
        safeApiCall { apiService.getShopPageData(forceRefresh) }

}