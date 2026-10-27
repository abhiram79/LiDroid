/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Metric tile: rounded tonal card with a live line chart and colored legend
 * rows. Pure containment + typography — colors come from the active scheme.
 */
package com.abhiram.lidroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import com.abhiram.lidroid.ui.theme.LiDroidShape

data class MetricLegend(
    val label: String,
    val value: String,
    val color: Color,
    val subtitle: String? = null,
)

@Composable
fun MetricChartCard(
    title: String,
    legends: List<MetricLegend>,
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    yMax: Float? = null,
    unavailable: String? = null,
    timeHint: String? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = LiDroidShape.Large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(LiDroidTokens.Spacing.LG),
            verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.SM),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (unavailable != null) {
                Text(
                    text = unavailable,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = LiDroidTokens.Spacing.LG),
                )
            } else {
                LiveLineChart(series = series, yMax = yMax)
                if (timeHint != null) {
                    Text(
                        text = timeHint,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            legends.forEach { legend ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(4.dp)
                            .height(28.dp)
                            .background(legend.color, LiDroidShape.ExtraSmall),
                    )
                    Spacer(modifier = Modifier.width(LiDroidTokens.Spacing.SM))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = legend.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (legend.subtitle != null) {
                            Text(
                                text = legend.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text(
                        text = legend.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = LiDroidTokens.mono(),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
