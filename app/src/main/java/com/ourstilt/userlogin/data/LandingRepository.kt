package com.ourstilt.userlogin.data

import android.net.Uri
import com.ourstilt.base.data.api.ApiService
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.data.repository.BaseRepository
import com.ourstilt.base.data.repository.DataStoreRepository
import com.ourstilt.deeplink.DeepLinkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class LandingRepository @Inject constructor(
    private val apiService: ApiService, private val dataStoreRepository: DataStoreRepository
) : BaseRepository() {

    suspend fun isUserLoggedIn(): Boolean {
        return dataStoreRepository.isUserLoggedIn.first()
    }

    fun getDeepLinkResponse(deepLink: Uri): Flow<NetworkResult<DeepLinkResponse>> =
        safeApiCall { apiService.fetchDeepLinkData(deepLink.toString()) }
}