package com.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

class KtorNetworkingClient(
    private val client: HttpClient,
    private val baseUrl: String,
) : NetworkingClient {

    override suspend fun get(uri: String, parameters: Map<String, String>): HttpResponse {
        return client.get(baseUrl + uri) {
            url {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
}
