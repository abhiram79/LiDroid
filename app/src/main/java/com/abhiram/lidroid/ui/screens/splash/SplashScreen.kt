/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Launch splash: pure black canvas with the "LiDroid" wordmark centered in
 * white (no icon, no subheading). The wordmark springs in, holds, then fades
 * out and hands off to the home (or setup) entry — one uninterrupted motion
 * from the black system launch screen into the app.
 */
package com.abhiram.lidroid.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import com.abhiram.lidroid.ui.theme.LiDroidMotion
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import com.abhiram.lidroid.ui.theme.LiDroidSplashBlack
import com.abhiram.lidroid.ui.theme.LiDroidSplashText
import kotlinx.coroutines.delay

/** Minimum time the wordmark is held before the hand-off (ms). */
private const val MIN_HOLD_MS = 1_600L

@Composable
fun SplashScreen(
    // Provider lambda, not a plain Boolean: a Boolean parameter is captured
    // by value inside the LaunchedEffect below, so the wait loop would never
    // observe the state flip once the first emission lands and the app would
    // hang on the splash forever. The lambda re-reads the latest state on
    // every poll instead.
    setupKnown: () -> Boolean,
    onDone: () -> Unit,
) {
    var contentVisible by remember { mutableStateOf(false) }
    var exiting by remember { mutableStateOf(false) }

    // Wordmark entrance: fade + gentle scale-up on a bouncy spring.
    val titleScale by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.92f,
        animationSpec = LiDroidMotion.bouncy(),
        label = "splash_title_scale",
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard),
        label = "splash_title_alpha",
    )

    // Whole-screen exit fade so the destination fades in over black, never a cut.
    val exitAlpha by animateFloatAsState(
        targetValue = if (exiting) 0f else 1f,
        animationSpec = tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard),
        label = "splash_exit_alpha",
    )

    LaunchedEffect(Unit) {
        contentVisible = true
        // Hold long enough to read, then leave — but never before the start
        // destination is known (isSetupDone's first emission on the main
        // thread); waiting is invisible because the screen is already black.
        delay(MIN_HOLD_MS)
        while (!setupKnown()) delay(50)
        exiting = true
        delay(LiDroidMotion.Medium.toLong())
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LiDroidSplashBlack)
            .alpha(exitAlpha),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "LiDroid",
            style = MaterialTheme.typography.displaySmall,
            fontFamily = LiDroidTokens.ui(),
            fontWeight = FontWeight.Black,
            color = LiDroidSplashText,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = titleScale
                    scaleY = titleScale
                    alpha = titleAlpha
                },
        )
    }
}
