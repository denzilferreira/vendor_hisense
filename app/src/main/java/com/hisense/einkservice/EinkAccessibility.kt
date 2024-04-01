package com.hisense.einkservice

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.hisense.einkservice.ui.theme.HisenseTheme
import com.hisense.einkservice.ui.views.EinkOverlay
import com.hisense.einkservice.ui.views.MyLifecycleOwner

class EinkAccessibility : AccessibilityService() {

    private val einkService = IEinkServiceInterfaceImpl()

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    private val overlayParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        flags =
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.BOTTOM
        format = PixelFormat.TRANSPARENT
        y = 100
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()
            // TODO: look-up preferred speed and set automatically
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.scanCode == 766) {
            einkService.clearScreen()

            if (!::overlayView.isInitialized) {
                setupOverlay()
            }

            overlayView.visibility = View.VISIBLE
            return true
        }
        return super.onKeyEvent(event)
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private fun setupOverlay() {
        overlayView = ComposeView(this).apply {
            setContent {
                EinkOverlay(
                    onClear = {
                        einkService.setSpeed(EinkSpeed.CLEAR.getSpeed())
                        overlayView.visibility = View.GONE
                    },
                    onBalanced = {
                        einkService.setSpeed(EinkSpeed.BALANCED.getSpeed())
                        overlayView.visibility = View.GONE
                    },
                    onSmooth = {
                        einkService.setSpeed(EinkSpeed.SMOOTH.getSpeed())
                        overlayView.visibility = View.GONE
                    },
                    onFast = {
                        einkService.setSpeed(EinkSpeed.FAST.getSpeed())
                        overlayView.visibility = View.GONE
                    }
                )
            }
        }

        overlayView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                event?.let {
                    if (event.action == MotionEvent.ACTION_OUTSIDE) {
                        overlayView.visibility = View.GONE
                        view?.performClick()
                        return true
                    }
                }
                view?.performClick()
                return true
            }
        })

        val viewModelStoreOwner = object : ViewModelStoreOwner {
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Eink Service interrupted", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    companion object {
        var isRunning: Boolean = false
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OverlayPreview() {
    HisenseTheme {
        EinkOverlay(
            onClear = { -> },
            onBalanced = { -> },
            onSmooth = { -> },
            onFast = { -> }
        )
    }
}