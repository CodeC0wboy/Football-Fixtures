package com.app.settings.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.app.settings.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<SettingsItem>>(emptyList())
    val items: StateFlow<List<SettingsItem>> get() = _items

    init {
        _items.value = listOf(
            SettingsItem.OpenWeb(
                url = "https://www.google.com/",
                resId = R.string.settings_privacy_policy
            )
        )
    }
}

sealed class SettingsItem(@StringRes val titleRes: Int) {
    data class OpenWeb(val url: String, @StringRes val resId: Int) : SettingsItem(resId)
}