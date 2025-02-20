package com.mystilt.search.data

import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.api.NetworkResult
import com.mystilt.base.data.repository.BaseRepository
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
//kanfalfafg
}