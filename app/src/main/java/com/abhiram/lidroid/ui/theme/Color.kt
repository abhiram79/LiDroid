/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Semantic color tokens for LiDroid's fallback dark scheme.
 *
 * The app is dark-only: the system always renders the dark scheme, either
 * the Android 12+ wallpaper-derived dynamic one (Material You) or this
 * hand-tuned expressive palette when dynamic color is unavailable.
 * No light scheme exists by design — there is no appearance mode switch.
 */
package com.abhiram.lidroid.ui.theme

import androidx.compose.ui.graphics.Color

// ── Fallback expressive dark palette ─────────────────────────────────────────
// Deep neutral surfaces with a slight cool cast; violet primary tones replace
// the old fixed green accent. Values are tonal pairs, not raw hex soup: each
// container/on-container pair is readable in both directions.

val LiDroidPrimary               = Color(0xFFB9A7FC)
val LiDroidOnPrimary             = Color(0xFF241056)
val LiDroidPrimaryContainer      = Color(0xFF3E2B78)
val LiDroidOnPrimaryContainer    = Color(0xFFE7DEFF)

val LiDroidSecondary             = Color(0xFFC6C3D9)
val LiDroidOnSecondary           = Color(0xFF2F2D42)
val LiDroidSecondaryContainer    = Color(0xFF454259)
val LiDroidOnSecondaryContainer  = Color(0xFFE2DFF5)

val LiDroidTertiary              = Color(0xFFF2B8C5)
val LiDroidOnTertiary            = Color(0xFF4C1F2D)
val LiDroidTertiaryContainer     = Color(0xFF663544)
val LiDroidOnTertiaryContainer   = Color(0xFFFFD9E1)

val LiDroidError                 = Color(0xFFFFB4AB)
val LiDroidOnError               = Color(0xFF690005)
val LiDroidErrorContainer        = Color(0xFF93000A)
val LiDroidOnErrorContainer      = Color(0xFFFFDAD6)

val LiDroidBackground            = Color(0xFF0D0C14)
val LiDroidOnBackground          = Color(0xFFE5E1F2)

val LiDroidSurface               = Color(0xFF12101B)
val LiDroidOnSurface             = Color(0xFFE5E1F2)
val LiDroidSurfaceVariant        = Color(0xFF23212E)
val LiDroidOnSurfaceVariant      = Color(0xFFC7C3D4)

val LiDroidSurfaceContainerLowest  = Color(0xFF0A0911)
val LiDroidSurfaceContainerLow     = Color(0xFF17161F)
val LiDroidSurfaceContainer        = Color(0xFF1B1A25)
val LiDroidSurfaceContainerHigh    = Color(0xFF26252F)
val LiDroidSurfaceContainerHighest = Color(0xFF302F3A)

val LiDroidSurfaceDim             = Color(0xFF0D0C14)
val LiDroidSurfaceBright          = Color(0xFF383645)

val LiDroidInverseSurface         = Color(0xFFE5E1F2)
val LiDroidInverseOnSurface       = Color(0xFF221F2D)
val LiDroidInversePrimary         = Color(0xFF5A40B8)

val LiDroidOutline                = Color(0xFF918F9C)
val LiDroidOutlineVariant         = Color(0xFF454351)

val LiDroidScrim                  = Color(0xFF000000)

// Pure black is reserved for the launch/splash surface and the headless
// (server-mode) overlay so the app wakes up on an uninterrupted black frame.
val LiDroidSplashBlack            = Color(0xFF000000)
val LiDroidSplashText             = Color(0xFFFFFFFF)
