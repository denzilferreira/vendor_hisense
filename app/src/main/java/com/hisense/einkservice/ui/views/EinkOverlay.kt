package com.hisense.einkservice.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hisense.einkservice.ui.theme.HisenseTheme

@Composable
fun EinkOverlay(
    onClear: () -> Unit,
    onBalanced: () -> Unit,
    onSmooth: () -> Unit,
    onFast: () -> Unit,
) {
    Card(modifier = Modifier.wrapContentSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onClear) {
                Text("Clear", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onBalanced) {
                Text("Balanced", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onSmooth) {
                Text(text = "Smooth", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onFast) {
                Text(text = "Fast", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EinkDrawerPreview() {
    HisenseTheme {
        EinkOverlay(onClear = { -> }, onBalanced = { -> }, onSmooth = { -> }, onFast = { -> })
    }
}
