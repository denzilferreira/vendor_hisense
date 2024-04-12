package com.hisense.einkservice.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.ui.theme.HisenseTheme

@Composable
fun EinkOverlay(
    modifier: Modifier = Modifier,
    currentSpeed: EinkSpeed,
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            colors = if (currentSpeed == EinkSpeed.CLEAR) selectedColors else colors,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onClear
        ) {
            Text("Clear", style = MaterialTheme.typography.bodySmall)
        }
        OutlinedButton(
            colors = if (currentSpeed == EinkSpeed.BALANCED) selectedColors else colors,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onBalanced
        ) {
            Text("Balanced", style = MaterialTheme.typography.bodySmall)
        }
        OutlinedButton(
            colors = if (currentSpeed == EinkSpeed.SMOOTH) selectedColors else colors,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onSmooth
        ) {
            Text(text = "Smooth", style = MaterialTheme.typography.bodySmall)
        }
        OutlinedButton(
            colors = if (currentSpeed == EinkSpeed.FAST) selectedColors else colors,
            border = BorderStroke(2.dp, Color.Black),
            onClick = onFast
        ) {
            Text(text = "Fast", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EinkDrawerPreview() {
    HisenseTheme {
        EinkOverlay(
            currentSpeed = EinkSpeed.CLEAR,
            onClear = { -> }, onBalanced = { -> }, onSmooth = { -> }, onFast = { -> }
        )
    }
}
