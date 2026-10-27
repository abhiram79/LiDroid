/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import com.abhiram.lidroid.ui.theme.LiDroidShape

/**
 * Grouped settings card: the new containment unit. Related rows live inside
 * one rounded container (surfaceContainerLow) separated by whitespace — no
 * hairline dividers, no nesting more than one card deep.
 */
@Composable
fun LiDroidGroup(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = LiDroidShape.Large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(content = content)
    }
}

/**
 * Row inside a [LiDroidGroup]: label left, value or trailing control right.
 * 56 dp minimum height for comfortable touch targets; mono values for
 * IPs, ports, and paths.
 */
@Composable
fun LiDroidListRow(
    label: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    trailing: String? = null,
    mono: Boolean = false,
    onClick: (() -> Unit)? = null,
    rightSlot: @Composable (() -> Unit)? = null,
) {
    val rowMod = modifier
        .fillMaxWidth()
        .heightIn(min = 56.dp)
        .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
        .padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.MD)

    Row(
        modifier = rowMod,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (rightSlot != null) {
            rightSlot()
        } else if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = if (mono) LiDroidTokens.mono() else FontFamily.Default,
                textAlign = TextAlign.End,
                // fill = false: a short value hugs the right edge via SpaceBetween;
                // a long value wraps inside the leftover space instead of starving the label.
                modifier = Modifier.weight(1f, fill = false),
            )
            if (trailing != null) {
                Spacer(Modifier.width(LiDroidTokens.Spacing.SM))
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
