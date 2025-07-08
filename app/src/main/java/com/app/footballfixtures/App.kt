package com.app.footballfixtures

import android.app.Application
import com.app.core.config.configModule
import com.app.core.network.networkModule
import com.app.matches.di.matchesModule
import com.app.settings.settingsModule

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                matchesModule,
                settingsModule,
                configModule
            )
        }
    }
}
