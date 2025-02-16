package com.mystilt.shops.data

import com.mystilt.base.data.api.ApiService
import com.mystilt.base.data.repository.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {
}