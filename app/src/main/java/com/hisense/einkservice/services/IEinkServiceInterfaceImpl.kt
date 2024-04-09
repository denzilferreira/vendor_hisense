package com.hisense.einkservice.services

import android.util.Log
import com.hisense.einkservice.IEinkServiceInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IEinkServiceInterfaceImpl : IEinkServiceInterface.Stub() {
    private val TAG = IEinkServiceInterfaceImpl::class.java.getSimpleName()
    private val EINK_PATH = "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/"

    override fun setSpeed(speed: Int) {
        Log.i(TAG, "setting speed mode: $speed")
        writeToFile(speed.toString(), EINK_PATH + "epd_display_mode")
    }

    override fun clearScreen() {
        Log.i(TAG, "clearing screen")
        writeToFile("1", EINK_PATH + "epd_force_clear")
    }

    override fun getCurrentSpeed(): Int {
        val speed = readFromFile(EINK_PATH + "epd_display_mode")
        return speed.filter { it.isDigit() }.toInt()
    }

    private fun writeToFile(
        data: String,
        filename: String,
    ) {
        try {
            val file = File(filename)
            val stream = FileOutputStream(file)
            stream.use { io ->
                io.write(data.toByteArray())
            }
        } catch (e: IOException) {
            Log.e(TAG, "File write failed: $e")
        }
    }

    private fun readFromFile(
        filename: String,
    ): String {
        try {
            val file = File(filename)
            val stream = file.inputStream()
            return stream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "File read failed: $e")
        }
        return ""
    }
}
