/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Single source of truth for LiDroid design tokens. Theme.kt maps the color
 * tokens into MaterialTheme.colorScheme; screens consume colors via
 * MaterialTheme.colorScheme.* and consume the non-color tokens (spacing,
 * radii, fonts) directly from here.
 *
 * Spacing follows a 4 dp baseline grid. Radii follow the shape scale in
 * Shape.kt; keep both together so the geometry system stays coherent.
 */
package com.abhiram.lidroid.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

object LiDroidTokens {

    object Spacing {
        /** Micro gap: icon-to-text, inner chips. */
        val XS = 4.dp
        /** Small gap: between related rows, chip flow gaps. */
        val SM = 8.dp
        /** Standard intra-card gap. */
        val MD = 12.dp
        /** Screen side margin / card padding. */
        val LG = 16.dp
        /** Section separation. */
        val XL = 24.dp
        /** Large section break. */
        val XL2 = 32.dp
        /** Hero top/bottom breathing room. */
        val XL3 = 48.dp
    }

    object Radius {
        /** Tiny controls: extra-key pills, toggle tracks. */
        val XS = 8.dp
        /** Small: search fields, swatches. */
        val SM = 12.dp
        /** Medium: list sub-cards. */
        val MD = 16.dp
        /** Large: grouped cards, sheets' inner tiles. */
        val LG = 24.dp
        /** Extra large: bottom sheets, dialogs, hero cards. */
        val XL = 32.dp
        /** Fully rounded: buttons, chips, status badges (use CircleShape where a true pill is needed). */
        val Pill = 999.dp
    }

    val Accent = LiDroidPrimary
    val Red = LiDroidError

    @Volatile private var interFamily: FontFamily? = null

    fun interFamily(context: Context): FontFamily {
        interFamily?.let { return it }
        synchronized(this) {
            interFamily?.let { return it }
            val regular  = extractAsset(context, "ui-fonts/Inter-Regular.ttf")
            val semiBold = extractAsset(context, "ui-fonts/Inter-SemiBold.ttf")
            val fam = FontFamily(
                Font(regular,  FontWeight.Normal),
                Font(semiBold, FontWeight.SemiBold),
            )
            interFamily = fam
            return fam
        }
    }

    @Volatile private var monoFamily: FontFamily? = null

    fun monoFamily(context: Context): FontFamily {
        monoFamily?.let { return it }
        synchronized(this) {
            monoFamily?.let { return it }
            val mono = extractAsset(context, "fonts/JetBrains-Mono.ttf")
            val fam = FontFamily(Font(mono, FontWeight.Normal))
            monoFamily = fam
            return fam
        }
    }

    @Composable @ReadOnlyComposable
    fun ui(): FontFamily = interFamily(LocalContext.current)

    @Composable @ReadOnlyComposable
    fun mono(): FontFamily = monoFamily(LocalContext.current)

    private fun extractAsset(context: Context, assetPath: String): File {
        val outFile = File(context.filesDir, assetPath)
        if (outFile.exists() && outFile.length() > 0) return outFile
        outFile.parentFile?.mkdirs()
        // Write to a tmp file and rename onto outFile so a process killed
        // mid-copy never leaves a half-written font at the canonical path
        // (the exists()+length() check above would otherwise treat it as done).
        val tmpFile = File(outFile.parentFile, outFile.name + ".tmp")
        context.assets.open(assetPath).use { input ->
            FileOutputStream(tmpFile).use { output -> input.copyTo(output) }
        }
        if (!tmpFile.renameTo(outFile)) {
            tmpFile.delete()
            throw java.io.IOException("atomic rename ${tmpFile.name} -> ${outFile.name} failed")
        }
        return outFile
    }
}
