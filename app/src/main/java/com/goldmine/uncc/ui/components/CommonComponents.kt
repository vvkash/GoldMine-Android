package com.goldmine.uncc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/**
 * The signature GoldMine card: Charlotte-green outline plus a Niner-gold glow in dark mode,
 * reproducing the `.overlay(stroke).shadow(ninerGold)` treatment used across the iOS app.
 */
@Composable
fun Modifier.goldMineCard(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: androidx.compose.ui.unit.Dp = 3.dp,
    glow: Boolean = true,
): Modifier {
    val extras = LocalGoldMineColors.current
    return this
        .shadow(
            elevation = if (glow && extras.isDark) 10.dp else 4.dp,
            shape = shape,
            ambientColor = if (glow && extras.isDark) GoldMineColors.NinerGold else Color.Black,
            spotColor = if (glow && extras.isDark) GoldMineColors.NinerGold else Color.Black,
        )
        .clip(shape)
        .background(extras.cardBackground)
        .border(BorderStroke(borderWidth, GoldMineColors.CharlotteGreen), shape)
}

/** Icon inside a white circle with a Charlotte-green ring — the app's recurring motif. */
@Composable
fun CircleIcon(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    diameter: androidx.compose.ui.unit.Dp = 50.dp,
    ringWidth: androidx.compose.ui.unit.Dp = 3.dp,
) {
    val extras = LocalGoldMineColors.current
    Box(
        modifier = modifier
            .size(diameter)
            .clip(CircleShape)
            .background(extras.iconCircleFill)
            .border(ringWidth, GoldMineColors.CharlotteGreen, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(diameter * 0.48f),
        )
    }
}

/** Home-grid shortcut, matching the iOS `NavigationButton`. */
@Composable
fun NavigationButton(
    title: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(extras.cardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIcon(icon = icon, tint = accent)
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

/** Full-width row used on the Social hub, matching the iOS `SocialButton`. */
@Composable
fun SocialButton(
    icon: ImageVector,
    title: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(extras.cardBackground)
            .border(1.5.dp, GoldMineColors.CharlotteGreen, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIcon(icon = icon, tint = accent, diameter = 44.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GoldMineColors.AccentGray,
        )
    }
}

/**
 * Back / title / trailing header used by the screens that on iOS were plain `HStack`s rather
 * than navigation bars.
 */
@Composable
fun GoldMineHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    backLabel: String? = "Back",
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val accent = LocalGoldMineColors.current.accent
        Box(modifier = Modifier.width(96.dp), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBack)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                    if (backLabel != null) {
                        Text(
                            text = backLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = accent,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
        )

        Box(modifier = Modifier.width(96.dp), contentAlignment = Alignment.CenterEnd) {
            trailing?.invoke()
        }
    }
}

/** Small pill badge used for vote counts and status chips. */
@Composable
fun StatusPill(
    text: String,
    icon: ImageVector?,
    contentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
            )
        }
    }
}

/** Centred empty/`coming soon` state shared by several screens. */
@Composable
fun CenteredMessage(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(64.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalGoldMineColors.current.secondaryText,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        if (action != null) {
            Spacer(Modifier.size(8.dp))
            action()
        }
    }
}

/** Trailing icon button rendered in the Charlotte-green accent. */
@Composable
fun HeaderIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = LocalGoldMineColors.current.accent,
        )
    }
}
