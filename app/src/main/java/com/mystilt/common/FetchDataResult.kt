package com.mystilt.common

sealed class FetchDataResult<out T : Any> {
    data object Loading : FetchDataResult<Nothing>()
    data class Success<out T : Any>(val data: T) : FetchDataResult<T>()
    data class Exception(val exception: Throwable) : FetchDataResult<Nothing>()
}