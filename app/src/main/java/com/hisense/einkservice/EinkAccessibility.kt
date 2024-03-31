package com.hisense.einkservice

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
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

            overlayView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    event?.let {
                        if (event.action == MotionEvent.ACTION_OUTSIDE) {
                            windowManager.removeView(overlayView)
                            return true
                        }
                    }
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

            return true
        }
        return super.onKeyEvent(event)
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        overlayView = ComposeView(this).apply {
            setContent {
                EinkOverlay(
                    onClear = {
                        einkService.setSpeed(EinkSpeed.CLEAR.getSpeed())
                        windowManager.removeView(overlayView)
                    },
                    onBalanced = {
                        einkService.setSpeed(EinkSpeed.BALANCED.getSpeed())
                        windowManager.removeView(overlayView)
                    },
                    onSmooth = {
                        einkService.setSpeed(EinkSpeed.SMOOTH.getSpeed())
                        windowManager.removeView(overlayView)
                    },
                    onFast = {
                        einkService.setSpeed(EinkSpeed.FAST.getSpeed())
                        windowManager.removeView(overlayView)
                    }
                )
            }
        }
    }

    override fun onInterrupt() {
        Toast.makeText(this, "Eink Service interrupted", Toast.LENGTH_SHORT).show()
    }
}