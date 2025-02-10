package com.mystilt.base.data.api

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val errorCode: Int?, val message: String) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}