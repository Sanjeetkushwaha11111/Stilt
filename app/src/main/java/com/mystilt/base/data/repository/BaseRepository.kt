package com.mystilt.base.data.repository

import com.mystilt.base.data.api.ApiResponse
import com.mystilt.base.data.api.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

// 6. Base Repository
abstract class BaseRepository {
    protected fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): Flow<NetworkResult<T>> = flow {
        try {
            emit(NetworkResult.Loading())
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == true) {
                    body.data?.let {
                        emit(NetworkResult.Success(it))
                    } ?: run {
                        emit(NetworkResult.Error(null, "Data is null"))
                    }
                } else {
                    emit(
                        NetworkResult.Error(
                            body?.errorCode ?: response.code(),
                            body?.message ?: "Unknown error occurred"
                        )
                    )
                }
            } else {
                emit(NetworkResult.Error(response.code(), response.message()))
            }
        } catch (e: Exception) {
            emit(
                NetworkResult.Error(
                    when (e) {
                        is IOException -> -1
                        is SocketTimeoutException -> -2
                        else -> -3
                    }, e.message ?: "Unknown error occurred"
                )
            )
        }
    }

}