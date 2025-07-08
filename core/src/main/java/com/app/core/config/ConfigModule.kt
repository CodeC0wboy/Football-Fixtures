package com.app.core.config

import org.koin.dsl.module

val configModule = module {
    factory<RemoteConfig> {
        MockRemoteConfig()
    }
}