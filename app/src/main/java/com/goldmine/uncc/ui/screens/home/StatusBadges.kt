package com.goldmine.uncc.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.goldmine.uncc.core.colorFromHex
import com.goldmine.uncc.core.formatMinutesOfDay
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.data.model.GymOccupancy
import com.goldmine.uncc.data.model.busynessText
import com.goldmine.uncc.ui.components.goldMineCard
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import kotlin.math.roundToInt

/** Circular progress ring used by both the badge and the detailed UREC screen. */
@Composable
fun OccupancyRing(
    percentage: Double,
    diameter: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = Color.Gray.copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = GoldMineColors.CharlotteGreen,
                startAngle = -90f,
                sweepAngle = (percentage.coerceIn(0.0, 100.0) / 100.0 * 360.0).toFloat(),
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        content?.invoke()
    }
}

/** Home ticker showing predicted UREC busyness — the iOS `GymStatusBadgeView`. */
@Composable
fun GymStatusBadge(
    occupancy: GymOccupancy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .goldMineCard()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "UREC Status",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OccupancyRing(
                percentage = occupancy.occupancyPercentage,
                diameter = 40.dp,
                strokeWidth = 6.dp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${occupancy.occupancyPercentage.roundToInt()}% Full",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (occupancy.isClosed) {
                        "Closed"
                    } else {
                        busynessText(occupancy.occupancyPercentage)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = extras.secondaryText,
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "6:00 AM - 11:00 PM",
                style = MaterialTheme.typography.labelSmall,
                color = extras.secondaryText,
            )
        }
    }
}

/** Home ticker listing the first few classes scheduled today — the iOS `TodaysClassesBadgeView`. */
@Composable
fun TodaysClassesBadge(
    classes: List<ClassItem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val sorted = classes.sortedBy { it.startMinutes }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .goldMineCard()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Today's Classes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (sorted.isEmpty()) {
            Text(
                text = "No classes today",
                style = MaterialTheme.typography.bodyMedium,
                color = extras.secondaryText,
            )
        } else {
            // iOS lays these out in a fixed 4-column LazyVGrid, so a single class must still
            // occupy only a quarter of the width rather than stretching across the badge.
            val visible = sorted.take(4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                visible.forEach { item ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (extras.isDark) {
                                    Color.Black.copy(alpha = 0.3f)
                                } else {
                                    Color.Gray.copy(alpha = 0.1f)
                                },
                            )
                            .padding(horizontal = 4.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colorFromHex(item.color)),
                        )
                        Text(
                            text = item.name.take(4),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                        )
                    }
                }
                if (sorted.size > 4) {
                    Text(
                        text = "+${sorted.size - 4}",
                        style = MaterialTheme.typography.labelMedium,
                        color = extras.secondaryText,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    repeat(4 - visible.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            sorted.firstOrNull()?.let { next ->
                Text(
                    text = "${next.name} · ${formatMinutesOfDay(next.startMinutes)} · " +
                        "${next.buildingName} ${next.roomNumber}".trim(),
                    style = MaterialTheme.typography.labelSmall,
                    color = extras.secondaryText,
                    maxLines = 1,
                )
            }
        }
    }
}
