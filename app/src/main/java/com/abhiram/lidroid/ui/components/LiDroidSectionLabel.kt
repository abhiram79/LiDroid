/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abhiram.lidroid.ui.theme.LiDroidTokens

/**
 * Section heading above a group card. Sentence case in titleSmall — hierarchy
 * via size and weight instead of shouting capitals. Sits outside the card so
 * the label and its group read as one unit.
 */
@Composable
fun LiDroidSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .padding(top = LiDroidTokens.Spacing.XL, bottom = LiDroidTokens.Spacing.SM),
    )
}
