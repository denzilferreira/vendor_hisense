package com.hisense.einkservice.ui.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisense.einkservice.R
import com.hisense.einkservice.model.EinkApp
import com.hisense.einkservice.services.EinkAccessibility
import com.hisense.einkservice.ui.theme.HisenseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkMainActivityScreen(
    apps: List<EinkApp>,
    isOverlayGranted: Boolean,
    isAccessibilityEnabled: Boolean,
    onAccessibilityClicked: () -> Unit,
    onOverlayClicked: () -> Unit,
    onSwipe: (EinkApp) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eink Center", fontWeight = FontWeight.Bold)
                },
            )
        },
        content = { innerPadding ->
            Column(
                modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Welcome", style = MaterialTheme.typography.headlineSmall)
                Text("This app will allow you to adjust the preferred refresh speed of the e-ink display per app.")
                if (!isOverlayGranted) {
                    OverlayNotice(onClicked = onOverlayClicked)
                }
                if (!isAccessibilityEnabled) {
                    AccessibilityNotice(onClicked = onAccessibilityClicked)
                }
                if (apps.isEmpty()) {
                    TutorialNotice()
                }
                AppsList(apps = apps, onSwipe = onSwipe)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Made with ❤\uFE0F - Denzil Ferreira, and you? \uD83D\uDE09",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppsList(
    apps: List<EinkApp>,
    onSwipe: (app: EinkApp) -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = true,
    ) {
        items(apps.size, { index -> apps[index].packageName }) { index ->
            val item = apps[index]
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onSwipe(item)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                modifier = Modifier.animateContentSize(),
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    val success = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
                    val color by animateColorAsState(
                        if (success) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                        label = "color"
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                content = {
                    EinkAppItemView(
                        item = item,
                        onSetNewSpeed = { newSpeed ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val repository = EinkAccessibility.getRepository(context)
                                val einkApp = repository.getByPackageName(item.packageName)

                                einkApp?.let {
                                    einkApp.preferredSpeed = newSpeed.getSpeed()
                                    repository.update(einkApp)

                                    // apply immediately the new speed for Eink Center
                                    if (einkApp.packageName == context.packageName) {
                                        EinkAccessibility.einkService()
                                            .setSpeed(newSpeed.getSpeed())
                                    }
                                }
                            }
                        }
                    )
                },
                state = dismissState,
            )
        }
    }
}

@Composable
private fun AccessibilityNotice(onClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClicked,
        colors =
        CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "Tap here to activate Eink Center in Settings > Accessibility Services",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OverlayNotice(onClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClicked,
        colors =
        CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "Tap here to grant overlay permission. We need it to show the speed selector on top of any app.",
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TutorialNotice() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
        CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                "No apps yet \uD83D\uDE2D. Let's change that!",
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.eink_tutorial_text),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "This tutorial will disappear once you add an app. Let's GO! \uD83D\uDE80",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EinkActivityScreenPreview() {
    HisenseTheme {
        EinkMainActivityScreen(
            apps = emptyList(),
            isOverlayGranted = true,
            isAccessibilityEnabled = true,
            onAccessibilityClicked = { },
            onOverlayClicked = { },
            onSwipe = { },
        )
    }
}
