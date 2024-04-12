package com.hisense.einkservice.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.hisense.einkservice.R
import com.hisense.einkservice.model.EinkSpeed
import com.hisense.einkservice.model.EinkApp

@Composable
fun EinkAppItemView(
    item: EinkApp,
    onSetNewSpeed: (EinkSpeed) -> Unit,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val packageInfo = packageManager.getPackageInfo(item.packageName, 0)

    val appIcon = remember {
        val drawable = packageInfo.applicationInfo.loadIcon(packageManager)
        BitmapPainter(drawable.toBitmap().asImageBitmap())
    }

    val appName = remember {
        packageInfo.applicationInfo.loadLabel(packageManager).toString()
    }

    Card {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = appIcon,
                    contentDescription = appName,
                    contentScale = ContentScale.Fit,
                )
                Text(text = appName, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            MiniSpeedController(
                selected = EinkSpeed.fromSpeed(item.preferredSpeed),
                onClear = { onSetNewSpeed(EinkSpeed.CLEAR) },
                onBalanced = { onSetNewSpeed(EinkSpeed.BALANCED) },
                onSmooth = { onSetNewSpeed(EinkSpeed.SMOOTH) },
                onFast = { onSetNewSpeed(EinkSpeed.FAST) },
            )
        }
    }
}

@Composable
private fun MiniSpeedController(
    selected: EinkSpeed,
    onClear: () -> Unit,
    onBalanced: () -> Unit,
    onSmooth: () -> Unit,
    onFast: () -> Unit,
) {
    Row {
        IconButton(onClick = onClear) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.clear_mode),
                contentDescription = "Clear",
                tint = if (selected == EinkSpeed.CLEAR) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
            )
        }
        IconButton(onClick = onBalanced) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.balanced_mode),
                contentDescription = "Balanced",
                tint = if (selected == EinkSpeed.BALANCED) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
            )
        }
        IconButton(onClick = onSmooth) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.smooth_mode),
                contentDescription = "Smooth",
                tint = if (selected == EinkSpeed.SMOOTH) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
            )
        }
        IconButton(onClick = onFast) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.fast_mode),
                contentDescription = "Fast",
                tint = if (selected == EinkSpeed.FAST) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EinkAppItemViewPreview() {
    EinkAppItemView(
        item = EinkApp(
            packageName = "com.hisense.einkservice",
            preferredSpeed = EinkSpeed.FAST.getSpeed(),
        ),
        onSetNewSpeed = {},
    )
}