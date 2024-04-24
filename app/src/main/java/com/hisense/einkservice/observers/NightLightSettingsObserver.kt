package com.hisense.einkservice.observers

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.hisense.einkservice.services.EinkAccessibility
import com.hisense.einkservice.services.IEinkServiceInterfaceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NightLightSettingObserver(
    handler: Handler,
    private val context: Context,
) : ContentObserver(handler) {

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled

    init {
        _isEnabled.value = isNightLightEnabled(context)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri.toString().contains("night_display_activated")) {
            _isEnabled.value = isNightLightEnabled(context)
            Log.d("NightLightSettingObserver", "Night light is enabled: ${_isEnabled.value}")
        }
    }

    private fun isNightLightEnabled(context: Context): Boolean {
        val contentResolver = context.contentResolver
        return android.provider.Settings.Secure.getInt(contentResolver, "night_display_activated", 0) == 1
    }
}