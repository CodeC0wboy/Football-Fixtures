package com.app.core.utils

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.openCustomTab(url: String) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    intent.launchUrl(this, url.toUri())
}