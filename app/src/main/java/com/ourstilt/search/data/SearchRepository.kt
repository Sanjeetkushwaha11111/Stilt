package com.ourstilt.search.data

import com.ourstilt.base.data.api.ApiService
import com.ourstilt.base.data.api.NetworkResult
import com.ourstilt.base.data.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {

    fun getSearchPageData(forceRefresh: Boolean = false): Flow<NetworkResult<SearchPageData>> =
        safeApiCall { apiService.getSearchPageData(forceRefresh) }


    fun getTrendingPageData(forceRefresh: Boolean = false): Flow<NetworkResult<TrendingPageData>> =
        safeApiCall { apiService.getTrendingPageData(forceRefresh) }

}