package com.hisense.einkservice.ui.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hisense.einkservice.EinkSpeed
import com.hisense.einkservice.model.EinkApp

@Composable
fun EinkAppItemView(
    item: EinkApp,
    onSetNewSpeed: (EinkSpeed) -> Unit,
) {
    Row {
        Text(item.packageName)
    }
}