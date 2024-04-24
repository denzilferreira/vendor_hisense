package com.hisense.einkservice.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.hisense.einkservice.MainActivity
import com.hisense.einkservice.model.EinkApp
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.observers.NightLightIntensityObserver
import com.hisense.einkservice.observers.NightLightSettingObserver
import com.hisense.einkservice.repository.EinkAppDatabase
import com.hisense.einkservice.repository.EinkAppRepository
import com.hisense.einkservice.repository.EinkAppRepositoryImpl
import com.hisense.einkservice.ui.theme.HisenseTheme
import com.hisense.einkservice.ui.views.EinkOverlay
import com.hisense.einkservice.ui.views.MyLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class EinkAccessibility : AccessibilityService() {
    private val einkService = IEinkServiceInterfaceImpl()

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var repository: EinkAppRepository

    private lateinit var nightLightObserver: NightLightSettingObserver
    private lateinit var nightLightIntensityObserver: NightLightIntensityObserver

    private var lastClickTimestamp: Long = 0

    private var lastApp: String = ""
    private var currentApp: String = ""

    private val overlayParams =
        WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags =
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.BOTTOM
            format = PixelFormat.TRANSPARENT
        }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // ignore OS pop-ups which don't have package name
            if (event.packageName == null) return

            lastApp = currentApp
            currentApp = event.packageName.toString()

            CoroutineScope(Dispatchers.IO).launch {
                repository.getByPackageName(currentApp)?.let {
                    // only change speed if it's different than what it currently is
                    val currentSpeed = einkService.currentSpeed
                    if (currentSpeed != it.preferredSpeed) {
                        einkService.setSpeed(it.preferredSpeed)
                    }
                }
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {

        // We don't want to show overlay for LineageOS 21 - sperhoooo has his own implementation
        if (Build.VERSION.SDK_INT == 34) {
            return super.onKeyEvent(event)
        }

        if (!::overlayView.isInitialized) {
            setupOverlay()
        }

        val duration = System.currentTimeMillis() - lastClickTimestamp
        Log.d("EinkAccessibility", "Key event detected: ${event?.action.toString()} $duration")

        if (event?.action == KeyEvent.ACTION_DOWN && event.scanCode == 766) {
            if (lastClickTimestamp > 0 && System.currentTimeMillis() - lastClickTimestamp < 300) {
                Log.d("EinkAccessibility", "Double click detected")
                overlayView.visibility = View.VISIBLE
                lastClickTimestamp = 0
                return true
            }
        } else if (event?.action == KeyEvent.ACTION_UP && event.scanCode == 766) {
            if (lastClickTimestamp > 0 && System.currentTimeMillis() - lastClickTimestamp > 500) {
                Log.d("EinkAccessibility", "Long press detected")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                lastClickTimestamp = 0

                return true
            }

            if (lastClickTimestamp > 0 && System.currentTimeMillis() - lastClickTimestamp < 200) {
                Log.d("EinkAccessibility", "Single click detected")
                einkService.clearScreen()

                lastClickTimestamp = 0
            }
        }

        lastClickTimestamp = System.currentTimeMillis()

        return super.onKeyEvent(event)
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private fun setupOverlay() {
        overlayView =
            ComposeView(this).apply {
                setContent {
                    EinkOverlay(
                        modifier = Modifier.getBottomPadding(),
                        onClear = {
                            setSpeedForApp(speed = EinkSpeed.CLEAR.getSpeed())
                        },
                        onBalanced = {
                            setSpeedForApp(speed = EinkSpeed.BALANCED.getSpeed())
                        },
                        onSmooth = {
                            setSpeedForApp(speed = EinkSpeed.SMOOTH.getSpeed())
                        },
                        onFast = {
                            setSpeedForApp(speed = EinkSpeed.FAST.getSpeed())
                        },
                    )
                }
            }

        overlayView.setOnTouchListener(
            View.OnTouchListener { view, event ->
                event?.let {
                    if (event.action == MotionEvent.ACTION_OUTSIDE) {
                        overlayView.visibility = View.GONE
                        view?.performClick()
                        return@OnTouchListener true
                    }
                }
                view?.performClick()
                true
            },
        )

        val viewModelStoreOwner =
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore
                    get() = ViewModelStore()
            }
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        overlayView.setViewTreeLifecycleOwner(lifecycleOwner)
        overlayView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        overlayView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        windowManager.addView(overlayView, overlayParams)
        overlayView.visibility = View.GONE // hide by default
    }

    fun setSpeedForApp(broadcastApp: String? = null, speed: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val app = when {
                broadcastApp != null -> broadcastApp
                currentApp == lastApp -> currentApp
                currentApp == "com.hisense.einkservice" -> lastApp
                else -> currentApp
            }

            val einkApp = repository.getByPackageName(app)
            if (einkApp != null) {
                einkApp.preferredSpeed = speed
                repository.update(einkApp)
            } else {
                val newApp = EinkApp(app, speed)
                repository.insert(newApp)
            }

            launch(Dispatchers.Main) {
                einkService.setSpeed(speed)
                overlayView.invalidate()
                overlayView.visibility = View.GONE
            }
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = WeakReference(this)
        isRunning = true
        repository = getRepository(applicationContext)

        registerNightLightObserver()
        registerNightLightIntensityObserver()

        CoroutineScope(Dispatchers.IO).launch {
            nightLightObserver.isEnabled.collect { isEnabled ->
                if (isEnabled) {
                    //einkService.setTemperature(isNightLight = true, brightness = 0)
                } else {
                    //einkService.setTemperature(isNightLight = false, brightness = 0)
                }
            }
        }
    }

    private fun registerNightLightObserver() {
        val uri = Settings.Secure.getUriFor("night_display_activated")
        val handler  = Handler(Looper.getMainLooper())
        nightLightObserver = NightLightSettingObserver(handler, applicationContext)
        applicationContext.contentResolver.registerContentObserver(uri, false, nightLightObserver)
    }

    private fun registerNightLightIntensityObserver() {
        val uri = Settings.Secure.getUriFor("night_display_color_temperature")
        val handler  = Handler(Looper.getMainLooper())
        val nightLightIntensityObserver = NightLightIntensityObserver(handler, applicationContext)
        applicationContext.contentResolver.registerContentObserver(uri, false, nightLightIntensityObserver)
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Eink Service interrupted", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isRunning = false
        applicationContext.contentResolver.unregisterContentObserver(nightLightObserver)
        applicationContext.contentResolver.unregisterContentObserver(nightLightIntensityObserver)
    }

    companion object {
        var isRunning: Boolean = false

        fun getRepository(context: Context): EinkAppRepository {
            return EinkAppRepositoryImpl(EinkAppDatabase.getInstance(context).einkAppDao())
        }

        fun einkService() = IEinkServiceInterfaceImpl()

        private var instance: WeakReference<EinkAccessibility>? = null

        fun getInstance(): EinkAccessibility {
            return instance?.get() ?: synchronized(this) {
                val newInstance = EinkAccessibility()
                instance = WeakReference(newInstance)
                newInstance
            }
        }
    }
}

@Composable
fun Modifier.getBottomPadding(): Modifier {
    val view = LocalView.current
    val insets = remember { view.rootWindowInsets }
    val bottom = insets?.getInsets(WindowInsets.Type.navigationBars())?.bottom ?: 0
    return this.padding(bottom = bottom.dp)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OverlayPreview() {
    HisenseTheme {
        EinkOverlay(
            onClear = { },
            onBalanced = { },
            onSmooth = { },
            onFast = { },
        )
    }
}