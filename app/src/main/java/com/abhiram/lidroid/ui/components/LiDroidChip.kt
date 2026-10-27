/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable

/**
 * Shared FilterChip color scheme: selection is tonal (primary container), not
 * a loud filled pill, so chip groups read as calm scales of the same control.
 * Disabled state keeps its selected tint visible at reduced emphasis — a
 * "selected but locked" chip must stay distinguishable from the others.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiDroidChipColors(): SelectableChipColors = FilterChipDefaults.filterChipColors(
    containerColor                  = MaterialTheme.colorScheme.surfaceContainer,
    labelColor                      = MaterialTheme.colorScheme.onSurface,
    iconColor                       = MaterialTheme.colorScheme.primary,
    selectedContainerColor          = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor              = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor          = MaterialTheme.colorScheme.surfaceContainer,
    disabledLabelColor              = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledSelectedContainerColor  = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
)
