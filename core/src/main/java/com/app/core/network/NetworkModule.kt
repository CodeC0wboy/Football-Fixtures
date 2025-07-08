package com.app.core.network

import com.app.core.config.API_HOST_KEY
import com.app.core.config.API_KEY
import com.app.core.config.BASE_URL_KEY
import com.app.core.config.RemoteConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.gson.gson
import org.koin.dsl.module

val networkModule = module {
    single {
        val remoteConfig = get<RemoteConfig>()
        HttpClient(OkHttp) {
            install(ContentNegotiation) { gson() }
            install(Logging) { level = LogLevel.BODY }

            defaultRequest {
                header("x-rapidapi-key", remoteConfig.getString(API_KEY))
                header("x-rapidapi-host", remoteConfig.getString(API_HOST_KEY))
                url {
                    protocol = URLProtocol.HTTPS
                    host = remoteConfig.getString(API_HOST_KEY)
                }
            }
        }
    }

    single<NetworkingClient> {
        val remoteConfig = get<RemoteConfig>()
        KtorNetworkingClient(
            client = get(),
            baseUrl = remoteConfig.getString(BASE_URL_KEY)
        )
    }
}