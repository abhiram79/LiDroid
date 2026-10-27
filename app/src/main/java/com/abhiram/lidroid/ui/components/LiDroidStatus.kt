/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abhiram.lidroid.ui.theme.LiDroidTokens

/**
 * Colored dot + label — status at a glance in Home meta rows and headers.
 * The dot color is decoupled from the accent so a stopped state stays neutral
 * under any dynamic palette.
 */
@Composable
fun LiDroidStatus(
    label: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Spacer(Modifier.width(LiDroidTokens.Spacing.SM))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Semantic status dot colors, resolved from the active color scheme. */
object LiDroidStatusColors {
    val Running: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val Starting: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
    val Stopping: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
    val Stopped: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val Error: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error
}
