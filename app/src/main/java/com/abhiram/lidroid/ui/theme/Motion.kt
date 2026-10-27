/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Motion system for LiDroid's expressive design.
 *
 * Reusable duration, easing, and spring tokens. Motion communicates cause and
 * effect: transitions are quick, springy, and interruptible — never decorative
 * filler. Screens consume these tokens instead of inventing tween/spring
 * parameters inline.
 */
package com.abhiram.lidroid.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object LiDroidMotion {

    // ── Durations (ms) ───────────────────────────────────────────────────────
    /** Micro feedback: ripples, toggles, selection states. */
    const val Fast = 200

    /** Standard container and navigation transitions. */
    const val Medium = 320

    /** Large surfaces entering/leaving (splash hand-off, expanded content). */
    const val Slow = 480

    /** Screen-to-screen movement when a heavy view swaps out. */
    const val XLarge = 600

    // ── Easings ──────────────────────────────────────────────────────────────
    /** Standard ease for general property animation. */
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Entering elements accelerate into place. */
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /** Exiting elements start fast, slow near the end. */
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /** No easing — the driver animates frame-by-frame (e.g. choreographed code). */
    val Linear: Easing = LinearEasing

    // ── Spring configurations ────────────────────────────────────────────────
    /** Bouncy spring for hero/status elements — expressive, small overshoot. */
    fun <T> bouncy() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    /** Default interactive spring — snappy with a hint of bounce. */
    fun <T> standard() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** Non-bouncy spring — value displays that must not overshoot. */
    fun <T> crisp() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )
}
