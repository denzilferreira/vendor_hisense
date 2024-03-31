package com.hisense.einkservice.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkMainActivityScreen() {
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
                .padding(16.dp)) {
                Text("Welcome", style = MaterialTheme.typography.headlineSmall)
                Text("This app will allow you to adjust the preferred refresh speed of the e-ink display per app.")
                Text("Activate Eink Center in Settings > Accessibility > Eink Center.", fontWeight = FontWeight.Bold)
                Text("WIP: This is a work in progress.")
                Spacer(Modifier.weight(1f))
                Text("Copyright © 2021 Denzil Ferreira", style = MaterialTheme.typography.labelSmall)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EinkActivityScreenPreview() {
    MaterialTheme {
        Surface {
            EinkMainActivityScreen()
        }
    }
}