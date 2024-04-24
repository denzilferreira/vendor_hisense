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

class NightLightIntensityObserver(
    handler: Handler,
    private val context: Context,
) : ContentObserver(handler) {

    private val MIN = 4082
    private val MAX = 2596

    private val _intensity = MutableStateFlow(0)
    val intensity: StateFlow<Int> = _intensity

    init {
        _intensity.value = getNightDisplayIntensity(context)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri.toString().contains("night_display_color_temperature")) {
            _intensity.value = getNightDisplayIntensity(context)
            Log.d("NightLightIntensityObserver", "Night light is: ${_intensity.value}")
        }
    }

    private fun getNightDisplayIntensity(context: Context): Int {
        val contentResolver = context.contentResolver
        val currentValue = android.provider.Settings.Secure.getInt(contentResolver, "night_display_color_temperature", 0)
        return scaleValue(MIN, MAX, currentValue)
    }

    private fun scaleValue(min: Int, max: Int, currentValue: Int): Int {
        val originalRange = min - max
        val scaledValue = ((currentValue - max).toDouble() / originalRange) * 100
        return (100 - scaledValue).toInt()
    }
}