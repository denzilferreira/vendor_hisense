package com.hisense.einkservice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleEventObserver
import com.hisense.einkservice.services.EinkAccessibility
import com.hisense.einkservice.ui.theme.HisenseTheme
import com.hisense.einkservice.ui.views.EinkMainActivityScreen
import com.hisense.einkservice.viewmodel.MainActivityViewModel
import com.hisense.einkservice.viewmodel.MainActivityViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isOverlayGranted by remember {
                mutableStateOf(false)
            }
            var isAccessibilityEnabled by remember {
                mutableStateOf(false)
            }

            val repository = EinkAccessibility.getRepository(applicationContext)
            val viewModel by viewModels<MainActivityViewModel> {
                MainActivityViewModelFactory(repository)
            }
            val apps by viewModel.apps.collectAsState(initial = emptyList())

            HisenseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    EinkMainActivityScreen(
                        apps = apps,
                        isOverlayGranted = isOverlayGranted,
                        isAccessibilityEnabled = isAccessibilityEnabled,
                        onAccessibilityClicked = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onOverlayClicked = {
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName"),
                                ),
                            )
                        },
                    )
                }
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer =
                    LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            isOverlayGranted = Settings.canDrawOverlays(application)
                            isAccessibilityEnabled = EinkAccessibility.isRunning
                        }
                    }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AppPreview() {
    HisenseTheme {
        EinkMainActivityScreen(
            apps = emptyList(),
            isOverlayGranted = false,
            isAccessibilityEnabled = false,
            onAccessibilityClicked = { },
            onOverlayClicked = { },
        )
    }
}
