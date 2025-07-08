package com.app.core.network.model

sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>

    sealed interface Failure : NetworkResult<Nothing> {
        data class Server(
            val code: Int,
            val rawMessage: String
        ) : Failure

        data class Internal(val throwable: Throwable) : Failure
    }
}
