/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abhiram.lidroid.R
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import com.abhiram.lidroid.util.AppPermission

/**
 * One row per applicable permission: title + why on the left, a "Granted" label
 * or a compact tonal Grant pill on the right. Owns no permission logic — the
 * caller decides which permissions apply, how to check them, and what Grant does.
 * Designed to sit inside a [LiDroidGroup].
 */
@Composable
fun PermissionRows(
    permissions: List<AppPermission>,
    isGranted: (AppPermission) -> Boolean,
    onGrant: (AppPermission) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = LiDroidTokens.Spacing.LG)) {
        permissions.forEachIndexed { index, permission ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LiDroidTokens.Spacing.MD),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(permission.titleRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(permission.whyRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(LiDroidTokens.Spacing.SM))
                if (isGranted(permission)) {
                    Text(
                        text = stringResource(R.string.perm_granted),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    LiDroidTextAction(
                        text = stringResource(R.string.perm_grant),
                        onClick = { onGrant(permission) },
                    )
                }
            }
            if (index != permissions.lastIndex) {
                Spacer(Modifier.height(LiDroidTokens.Spacing.SM))
            }
        }
    }
}
