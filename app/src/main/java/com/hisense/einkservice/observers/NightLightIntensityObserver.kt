package com.hisense.einkservice.observers

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NightLightIntensityObserver(
    handler: Handler,
    private val context: Context,
) : ContentObserver(handler) {

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
        return scaleValue(currentValue)
    }

    private fun scaleValue(currentValue: Int): Int {
        val originalRange = MIN_BRIGHTNESS_VALUE - MAX_BRIGHTNESS_VALUE
        val scaledValue = ((currentValue - MAX_BRIGHTNESS_VALUE).toDouble() / originalRange) * 100
        return (100 - scaledValue).toInt()
    }

    companion object {
        private val MIN_BRIGHTNESS_VALUE = 4082
        private val MAX_BRIGHTNESS_VALUE = 2596
        fun originalScale(scaledValue: Int): Int {
            val originalRange = MIN_BRIGHTNESS_VALUE - MAX_BRIGHTNESS_VALUE
            val currentValue = (100 - scaledValue).toDouble()
            return (currentValue / 100 * originalRange + MAX_BRIGHTNESS_VALUE).toInt()
        }
    }
}