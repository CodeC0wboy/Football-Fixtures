package com.app.core.config

interface RemoteConfig {
    fun getString(key: String): String
}

const val API_KEY = "api_key"
const val API_HOST_KEY = "api_host"
const val BASE_URL_KEY = "base_url"

/**
 * ⚠️ Warning: This is a mock implementation of RemoteConfigWrapper.
 * It's used temporarily due to lack of time to integrate a real Remote Config solution.
 *
 * In the future, this should be replaced with a proper implementation (e.g., FirebaseRemoteConfigWrapper),
 * which fetches config values remotely and supports runtime updates without requiring app releases.
 */
class MockRemoteConfig : RemoteConfig {

    private val config = mapOf(
        API_KEY to "f59d541878249c7600212d4249f03d2c",
        API_HOST_KEY to "v3.football.api-sports.io",
        BASE_URL_KEY to "https://v3.football.api-sports.io/"
    )

    override fun getString(key: String): String {
        return config[key] ?: ""
    }
}
