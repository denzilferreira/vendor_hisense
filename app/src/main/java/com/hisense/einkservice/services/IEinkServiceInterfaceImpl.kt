package com.hisense.einkservice.services

import android.util.Log
import com.hisense.einkservice.IEinkServiceInterface
import com.hisense.einkservice.observers.NightLightIntensityObserver
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IEinkServiceInterfaceImpl : IEinkServiceInterface.Stub() {
    private val TAG = IEinkServiceInterfaceImpl::class.java.getSimpleName()
    private val EINK_PATH = "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/"

    private val YELLOW_LED = "/sys/devices/platform/soc/4a84000.i2c/i2c-1/1-0036/backlight/aw99703-bl-1/brightness"
    private val WHITE_LED = "/sys/devices/platform/soc/4a80000.i2c/i2c-0/0-0036/backlight/aw99703-bl-2/brightness"

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

    override fun setTemperature(isNightLight: Boolean, brightness: Int) {
        val originalScale = NightLightIntensityObserver.originalScale(brightness)
        if (isNightLight) {
            setNightLight(true)
            writeToFile(originalScale.toString(), YELLOW_LED)
        } else {
            setNightLight(false)
            writeToFile(originalScale.toString(), WHITE_LED)
        }
    }

    private fun setNightLight(enabled: Boolean) {
        if (enabled) {
            writeToFile(0.toString(), WHITE_LED)
        } else {
            writeToFile(0.toString(), YELLOW_LED)
        }
    }

    override fun isNightLight(): Boolean {
        val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return whiteBrightness == 0 && yellowBrightness > 0
    }

    override fun getBrightness(): Int {
        val whiteBrightness = readFromFile(WHITE_LED).filter { it.isDigit() }.toInt()
        val yellowBrightness = readFromFile(YELLOW_LED).filter { it.isDigit() }.toInt()
        return if (whiteBrightness == 0) {
            yellowBrightness
        } else {
            whiteBrightness
        }
    }

    override fun setLockedScreen(lockscreen: CharArray?) {
        // TODO
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
