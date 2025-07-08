package com.app.core.network

import io.ktor.client.statement.HttpResponse

interface NetworkingClient {
    suspend fun get(uri: String, parameters: Map<String, String>): HttpResponse
}
