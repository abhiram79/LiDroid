/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Switch locked to the scheme's primary roles (dynamic-color aware). The off
 * state uses surface container tones so a resting switch reads as neutral.
 */
@Composable
fun LiDroidSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor    = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor    = MaterialTheme.colorScheme.primary,
            checkedBorderColor   = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor  = MaterialTheme.colorScheme.outline,
            uncheckedTrackColor  = MaterialTheme.colorScheme.surfaceContainerHighest,
            uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}
