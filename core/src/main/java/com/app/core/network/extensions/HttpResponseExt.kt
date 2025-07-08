package com.app.core.network.extensions

import com.app.core.network.model.NetworkResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.toResult(): NetworkResult<T> {
    return try {
        if (status.isSuccess()) {
            val parsedBody: T = body()
            NetworkResult.Success(parsedBody)
        } else {
            val rawError = bodyAsText()
            val code = status.value
            NetworkResult.Failure.Server(
                code = code,
                rawMessage = rawError
            )
        }
    } catch (e: Throwable) {
        NetworkResult.Failure.Internal(e)
    }
}
