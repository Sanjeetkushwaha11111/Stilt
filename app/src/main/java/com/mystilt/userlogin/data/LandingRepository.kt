package com.mystilt.userlogin.data

import android.net.Uri
import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.BaseRepository
import com.mystilt.base.data.repository.DataStoreRepository
import com.mystilt.deeplink.DeepLinkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class LandingRepository @Inject constructor(
    private val apiService: ApiService, private val dataStoreRepository: DataStoreRepository
) : BaseRepository() {

    suspend fun isUserLoggedIn(): Boolean {
        return dataStoreRepository.isUserLoggedIn.first()
    }

    fun getDeepLinkResponse(forceRefresh: Boolean, deepLink: Uri): Flow<NetworkResult<DeepLinkResponse>> =
        safeApiCall { apiService.fetchDeepLinkData(forceRefresh,deepLink.toString()) }
}