/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Shape scale for LiDroid's expressive design system.
 *
 * Centralized so the whole interface can be rethemed without touching
 * individual components. Corner geometry grows with component scale: pill and
 * fully-round shapes carry the expressive identity, while dialogs and sheets
 * stay large-but-controlled.
 */
package com.abhiram.lidroid.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object LiDroidShape {
    val ExtraSmall  = RoundedCornerShape(8.dp)
    val Small       = RoundedCornerShape(12.dp)
    val Medium      = RoundedCornerShape(16.dp)
    val Large       = RoundedCornerShape(24.dp)
    val ExtraLarge  = RoundedCornerShape(32.dp)
    /** Fully rounded pill — buttons, chips, status badges. */
    val Pill        = CircleShape
}

val LiDroidShapes = Shapes(
    extraSmall = LiDroidShape.ExtraSmall,
    small = LiDroidShape.Small,
    medium = LiDroidShape.Medium,
    large = LiDroidShape.Large,
    extraLarge = LiDroidShape.ExtraLarge,
)
