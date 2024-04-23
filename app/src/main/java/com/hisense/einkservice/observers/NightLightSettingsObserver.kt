package com.hisense.einkservice.observers

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.hisense.einkservice.services.EinkAccessibility
import com.hisense.einkservice.services.IEinkServiceInterfaceImpl

class NightLightSettingObserver(
    handler: Handler,
    private val context: Context,
    private val einkService: IEinkServiceInterfaceImpl = EinkAccessibility.einkService(),
) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri.toString().contains("night_display_activated")) {
            val isEnabled = isNightLightEnabled(context)
            Log.d("NightLightSettingObserver", "Night light is enabled: $isEnabled")
        }
    }

    private fun isNightLightEnabled(context: Context): Boolean {
        val contentResolver = context.contentResolver
        val uri = android.provider.Settings.Secure.getUriFor("night_display_activated")
        return android.provider.Settings.Secure.getInt(contentResolver, "night_display_activated", 0) == 1
    }
}