package com.hisense.einkservice.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.services.EinkAccessibility
import com.hisense.einkservice.ui.theme.HisenseTheme

@Composable
fun EinkOverlay(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onClear: () -> Unit,
    onBalanced: () -> Unit,
    onSmooth: () -> Unit,
    onFast: () -> Unit,
) {
    val colors = ButtonColors(
        containerColor = Color.White,
        contentColor = Color.Black,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.White,
    )

    val selectedColors = ButtonColors(
        containerColor = Color.Black,
        contentColor = Color.White,
        disabledContainerColor = Color.Gray,
        disabledContentColor = Color.White,
    )

    var speed : EinkSpeed by remember { mutableStateOf(EinkSpeed.FAST) }

    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                colors = if (speed == EinkSpeed.CLEAR) selectedColors else colors,
                border = BorderStroke(2.dp, Color.Black),
                onClick = onClear
            ) {
                Text("Clear", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
                colors = if (speed == EinkSpeed.BALANCED) selectedColors else colors,
                border = BorderStroke(2.dp, Color.Black),
                onClick = onBalanced
            ) {
                Text("Balanced", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
                colors = if (speed == EinkSpeed.SMOOTH) selectedColors else colors,
                border = BorderStroke(2.dp, Color.Black),
                onClick = onSmooth
            ) {
                Text(text = "Smooth", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
                colors = if (speed == EinkSpeed.FAST) selectedColors else colors,
                border = BorderStroke(2.dp, Color.Black),
                onClick = onFast
            ) {
                Text(text = "Fast", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EinkDrawerPreview() {
    HisenseTheme {
        EinkOverlay(
            onClear = { }, onBalanced = { }, onSmooth = { }, onFast = { }
        )
    }
}
