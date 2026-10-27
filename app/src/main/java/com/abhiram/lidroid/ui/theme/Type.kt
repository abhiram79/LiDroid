/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Semantic typography for LiDroid's expressive design system.
 *
 * All 15 Material 3 type roles are defined explicitly against the M3 baseline
 * scale (sizes/line heights/letter spacing), carried by Inter (bundled as a
 * ui-font asset and resolved at composition time — see LiDroidTokens.ui()).
 * Weights stay within the assets' Regular/SemiBold range; "Medium" resolves to
 * the nearest available cut. Monospace roles live in LiDroidTokens.mono().
 *
 * This is the single typography source of truth — screens must never hardcode
 * font sizes; they consume MaterialTheme.typography.*.
 */
package com.abhiram.lidroid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Baseline M3 type scale, tuned slightly tighter for Inter. */
private data class RoleSpec(
    val size: Int,
    val lineHeight: Int,
    val weight: FontWeight,
    val spacing: Double,
)

private object Roles {
    val displayLarge   = RoleSpec(57, 64, FontWeight.SemiBold, -0.25)
    val displayMedium  = RoleSpec(45, 52, FontWeight.SemiBold, -0.25)
    val displaySmall   = RoleSpec(36, 44, FontWeight.SemiBold, 0.0)
    val headlineLarge  = RoleSpec(32, 40, FontWeight.SemiBold, 0.0)
    val headlineMedium = RoleSpec(28, 36, FontWeight.SemiBold, 0.0)
    val headlineSmall  = RoleSpec(24, 32, FontWeight.SemiBold, 0.0)
    val titleLarge     = RoleSpec(22, 28, FontWeight.SemiBold, 0.0)
    val titleMedium    = RoleSpec(16, 24, FontWeight.Medium, 0.15)
    val titleSmall     = RoleSpec(14, 20, FontWeight.Medium, 0.1)
    val bodyLarge      = RoleSpec(16, 24, FontWeight.Normal, 0.5)
    val bodyMedium     = RoleSpec(14, 20, FontWeight.Normal, 0.25)
    val bodySmall      = RoleSpec(12, 16, FontWeight.Normal, 0.4)
    val labelLarge     = RoleSpec(14, 20, FontWeight.Medium, 0.1)
    val labelMedium    = RoleSpec(12, 16, FontWeight.Medium, 0.5)
    val labelSmall     = RoleSpec(11, 16, FontWeight.Medium, 0.5)
}

private fun role(ui: androidx.compose.ui.text.font.FontFamily, spec: RoleSpec): TextStyle = TextStyle(
    fontFamily = ui,
    fontWeight = spec.weight,
    fontSize = spec.size.sp,
    lineHeight = spec.lineHeight.sp,
    letterSpacing = spec.spacing.sp,
)

/**
 * Build the full Typography. @Composable so the asset-backed Inter family can
 * be resolved via LocalContext (fonts live in assets, extracted on first use).
 */
@Composable
@ReadOnlyComposable
fun buildLiDroidTypography(): Typography {
    val ui = LiDroidTokens.ui()
    return Typography(
        displayLarge   = role(ui, Roles.displayLarge),
        displayMedium  = role(ui, Roles.displayMedium),
        displaySmall   = role(ui, Roles.displaySmall),
        headlineLarge  = role(ui, Roles.headlineLarge),
        headlineMedium = role(ui, Roles.headlineMedium),
        headlineSmall  = role(ui, Roles.headlineSmall),
        titleLarge     = role(ui, Roles.titleLarge),
        titleMedium    = role(ui, Roles.titleMedium),
        titleSmall     = role(ui, Roles.titleSmall),
        bodyLarge      = role(ui, Roles.bodyLarge),
        bodyMedium     = role(ui, Roles.bodyMedium),
        bodySmall      = role(ui, Roles.bodySmall),
        labelLarge     = role(ui, Roles.labelLarge),
        labelMedium    = role(ui, Roles.labelMedium),
        labelSmall     = role(ui, Roles.labelSmall),
    )
}
