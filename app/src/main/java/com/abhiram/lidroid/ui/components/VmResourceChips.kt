/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * VM resource pickers (RAM, CPU, bandwidth, storage) rendered as pill chip
 * flows inside grouped cards. Each block: title row with the live value, then
 * the selectable options. Options come from DeviceResourcePolicy so a device
 * never offers more than it actually has.
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.abhiram.lidroid.R
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import com.abhiram.lidroid.ui.theme.LiDroidShape
import com.abhiram.lidroid.util.DeviceResourcePolicy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VmRamChips(
    currentMb: Int,
    onChange: (Int) -> Unit,
    enabled: Boolean = true,
) {
    Column(modifier = Modifier.padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.SM)) {
        Text(
            "${stringResource(R.string.ram_label)}  ·  ${formatRam(currentMb)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = LiDroidTokens.Spacing.SM),
        )
        val totalRamMb = DeviceResourcePolicy.deviceTotalRamMb(LocalContext.current)
        val ramOptions = DeviceResourcePolicy.ramOptionsFor(totalRamMb).let { options ->
            if (currentMb in options) options else options + currentMb
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
            verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
        ) {
            ramOptions.forEach { mb ->
                FilterChip(
                    selected = mb == currentMb,
                    enabled = enabled,
                    onClick = { onChange(mb) },
                    label = { Text(formatRam(mb)) },
                    shape = LiDroidShape.Pill,
                    colors = LiDroidChipColors(),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VmCpuChips(
    currentCpus: Int,
    onChange: (Int) -> Unit,
    enabled: Boolean = true,
) {
    Column(modifier = Modifier.padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.SM)) {
        Text(
            "${stringResource(R.string.cpu_cores)}  ·  $currentCpus",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = LiDroidTokens.Spacing.SM),
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
            verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
        ) {
            DeviceResourcePolicy.CPU_OPTIONS.forEach { n ->
                FilterChip(
                    selected = n == currentCpus,
                    enabled = enabled,
                    onClick = { onChange(n) },
                    label = { Text("$n") },
                    shape = LiDroidShape.Pill,
                    colors = LiDroidChipColors(),
                )
            }
        }
        // The high chips read as "more power" and are the opposite under emulation,
        // so say so where the choice is made rather than leaving it to be discovered.
        Text(
            text = stringResource(R.string.cpu_cores_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = LiDroidTokens.Spacing.SM),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VmStorageChips(
    currentGb: Int,
    onChange: (Int) -> Unit,
    minGb: Int = 0,
    enabled: Boolean = true,
) {
    Column(modifier = Modifier.padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.SM)) {
        Text(
            "${stringResource(R.string.storage)}  ·  $currentGb GB",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = LiDroidTokens.Spacing.SM),
        )
        val availableGb = DeviceResourcePolicy.deviceAvailableStorageGb(LocalContext.current)
        val storageOptions = DeviceResourcePolicy.storageOptionsFor(availableGb).let { options ->
            if (currentGb in options) options else options + currentGb
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
            verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
        ) {
            storageOptions.forEach { gb ->
                FilterChip(
                    selected = gb == currentGb,
                    enabled = enabled && gb >= minGb,
                    onClick = { onChange(gb) },
                    label = { Text("$gb GB") },
                    shape = LiDroidShape.Pill,
                    colors = LiDroidChipColors(),
                )
            }
        }
        Text(
            text = stringResource(R.string.storage_grow_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = LiDroidTokens.Spacing.SM),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VmBandwidthChips(
    currentMbps: Int,
    onChange: (Int) -> Unit,
    enabled: Boolean = true,
) {
    Column(modifier = Modifier.padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.SM)) {
        Text(
            "${stringResource(R.string.bandwidth_limit)}  ·  ${formatBandwidth(currentMbps)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = LiDroidTokens.Spacing.SM),
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
            verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
        ) {
            DeviceResourcePolicy.BANDWIDTH_OPTIONS_MBPS.forEach { mbps ->
                FilterChip(
                    selected = mbps == currentMbps,
                    enabled = enabled,
                    onClick = { onChange(mbps) },
                    label = { Text(formatBandwidth(mbps)) },
                    shape = LiDroidShape.Pill,
                    colors = LiDroidChipColors(),
                )
            }
        }
    }
}

@Composable
private fun formatRam(mb: Int): String =
    if (mb >= 1024) "${mb / 1024} GB" else "$mb MB"

@Composable
private fun formatBandwidth(mbps: Int): String =
    if (mbps <= 0) stringResource(R.string.bandwidth_unlimited) else "$mbps Mbps"
