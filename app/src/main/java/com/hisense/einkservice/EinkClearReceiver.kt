package com.hisense.einkservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hisense.einkservice.services.EinkAccessibility

class EinkClearReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val einkService = EinkAccessibility.einkService()
        einkService.clearScreen()
    }
}