/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * LiDroid Compose theme — dark-only, Material-color-driven.
 *
 * The color interface is Material's, not a fixed accent: on Android 12+ the
 * wallpaper-derived dynamic dark scheme (Material You) is always used, giving
 * every device a coherent tonal palette. Devices below that fall back to the
 * hand-tuned expressive dark palette in Color.kt. The old fixed green accent
 * and the light scheme are gone; there is deliberately no theme-mode switch.
 */
package com.abhiram.lidroid.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LiDroidDarkScheme = darkColorScheme(
    primary                 = LiDroidPrimary,
    onPrimary               = LiDroidOnPrimary,
    primaryContainer        = LiDroidPrimaryContainer,
    onPrimaryContainer      = LiDroidOnPrimaryContainer,
    inversePrimary          = LiDroidInversePrimary,

    secondary               = LiDroidSecondary,
    onSecondary             = LiDroidOnSecondary,
    secondaryContainer      = LiDroidSecondaryContainer,
    onSecondaryContainer    = LiDroidOnSecondaryContainer,

    tertiary                = LiDroidTertiary,
    onTertiary              = LiDroidOnTertiary,
    tertiaryContainer       = LiDroidTertiaryContainer,
    onTertiaryContainer     = LiDroidOnTertiaryContainer,

    error                   = LiDroidError,
    onError                 = LiDroidOnError,
    errorContainer          = LiDroidErrorContainer,
    onErrorContainer        = LiDroidOnErrorContainer,

    background              = LiDroidBackground,
    onBackground            = LiDroidOnBackground,

    surface                 = LiDroidSurface,
    onSurface               = LiDroidOnSurface,
    surfaceVariant          = LiDroidSurfaceVariant,
    onSurfaceVariant        = LiDroidOnSurfaceVariant,
    surfaceDim              = LiDroidSurfaceDim,
    surfaceBright           = LiDroidSurfaceBright,
    surfaceContainerLowest  = LiDroidSurfaceContainerLowest,
    surfaceContainerLow     = LiDroidSurfaceContainerLow,
    surfaceContainer        = LiDroidSurfaceContainer,
    surfaceContainerHigh    = LiDroidSurfaceContainerHigh,
    surfaceContainerHighest = LiDroidSurfaceContainerHighest,

    outline                 = LiDroidOutline,
    outlineVariant          = LiDroidOutlineVariant,

    inverseSurface          = LiDroidInverseSurface,
    inverseOnSurface        = LiDroidInverseOnSurface,

    scrim                   = LiDroidScrim,
)

/**
 * The one theme entry point. Always dark; dynamic color is applied whenever the
 * platform can provide it (S+) so the interface follows Material color.
 */
@Composable
fun LiDroidTheme(content: @Composable () -> Unit) {
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        LiDroidDarkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = buildLiDroidTypography(),
        shapes      = LiDroidShapes,
        content     = content,
    )
}
