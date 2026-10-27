/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Explainer card for server (headless) mode. Modeled on X11SettingsSheet: a
 * ModalBottomSheet styled from LiDroidTokens. Describes the feature and how to
 * exit, with one primary action to enable it.
 */
package com.abhiram.lidroid.ui.screens.terminal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abhiram.lidroid.R
import com.abhiram.lidroid.ui.components.LiDroidPrimaryButton
import com.abhiram.lidroid.ui.theme.LiDroidShape
import com.abhiram.lidroid.ui.theme.LiDroidTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerModeSheet(onDismiss: () -> Unit, onEnable: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = LiDroidShape.ExtraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LiDroidTokens.Spacing.XL)
                .padding(bottom = LiDroidTokens.Spacing.XL),
        ) {
            Text(
                text = stringResource(R.string.server_mode),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(LiDroidTokens.Spacing.SM))
            Text(
                text = stringResource(R.string.server_mode_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(LiDroidTokens.Spacing.LG))
            LiDroidPrimaryButton(
                text = stringResource(R.string.server_mode_enable),
                onClick = { onEnable(); onDismiss() },
            )
        }
    }
}
