package com.hisense.einkservice

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hisense.einkservice.ui.theme.HisenseTheme
import com.hisense.einkservice.ui.views.EinkOverlay

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OverlayPreview() {
    HisenseTheme {
        EinkOverlay(
            onClear = { -> },
            onBalanced = { -> },
            onSmooth = { -> },
            onFast = { -> },
        )
    }
}
