package com.app.core.mapper

import com.app.core.network.model.NetworkResult

inline fun <T, R> NetworkResult<T>.mapSuccess(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(this.data))
        is NetworkResult.Failure -> this
    }
}