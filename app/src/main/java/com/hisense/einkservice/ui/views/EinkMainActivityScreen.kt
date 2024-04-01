package com.hisense.einkservice.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisense.einkservice.ui.theme.HisenseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkMainActivityScreen(
    isOverlayGranted: Boolean,
    isAccessibilityEnabled: Boolean,
    onAccessibilityClicked: () -> Unit,
    onOverlayClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eink Center", fontWeight = FontWeight.Bold)
                }
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Welcome", style = MaterialTheme.typography.headlineSmall)
                Text("This app will allow you to adjust the preferred refresh speed of the e-ink display per app.")
                if (!isOverlayGranted) {
                    OverlayNotice(onClicked = onOverlayClicked)
                }
                if (!isAccessibilityEnabled) {
                    AccessibilityNotice(onClicked = onAccessibilityClicked)
                }
                Text("WIP: This is a work in progress.")
                Spacer(Modifier.weight(1f))
                Text("Copyright © 2021 Denzil Ferreira", style = MaterialTheme.typography.labelSmall)
            }
        }
    )
}

@Composable
private fun AccessibilityNotice(onClicked: () -> Unit) {
    Card(
        onClick = onClicked,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ){
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Tap here to activate Eink Center in Settings > Accessibility > Eink Center.", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OverlayNotice(onClicked: () -> Unit) {
    Card(
        onClick = onClicked,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("In order to show the speed selector on top of any app, we need overlay permissions for that. Tap here to grant access.", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EinkActivityScreenPreview() {
    HisenseTheme {
        EinkMainActivityScreen(
            isOverlayGranted = false,
            isAccessibilityEnabled = false,
            onAccessibilityClicked = { -> },
            onOverlayClicked = { -> }
        )
    }
}