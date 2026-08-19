package com.goldmine.uncc.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldmine.uncc.data.model.FreebieEvent
import com.goldmine.uncc.ui.components.StatusPill
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import java.text.SimpleDateFormat
import java.util.Locale

/** Feed card for one reported freebie — the iOS `EnergyDrinkEventCard`. */
@Composable
fun FreebieCard(
    event: FreebieEvent,
    userName: String,
    onVote: () -> Unit,
    onNoVote: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val shape = RoundedCornerShape(12.dp)
    val hasVoted = userName in event.votedUserIds
    val hasNoVoted = userName in event.noVotedUserIds
    val locked = hasVoted || hasNoVoted || userName.isBlank()
    val dateText = remember(event.date) { CARD_DATE_FORMAT.format(event.date) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (extras.isDark) Color.Black.copy(alpha = 0.6f) else Color.White)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(extras.iconCircleFill)
                    .border(3.dp, GoldMineColors.CharlotteGreen, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\uD83C\uDF81", fontSize = 22.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = event.company,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = event.location.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = extras.secondaryText,
                )
            }

            StatusPill(
                text = "${event.votes}",
                icon = Icons.Filled.ThumbUp,
                contentColor = GoldMineColors.AccentBlue,
                containerColor = GoldMineColors.AccentBlue.copy(alpha = 0.2f),
            )
        }

        Text(
            text = dateText,
            style = MaterialTheme.typography.labelSmall,
            color = extras.secondaryText,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        MiniFreebieMap(
            event = event,
            onClick = onOpenMap,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp)),
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray.copy(alpha = 0.2f)),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CardAction(
                icon = Icons.Filled.Map,
                label = "Map",
                tint = GoldMineColors.CharlotteGreen,
                enabled = true,
                onClick = onOpenMap,
            )
            CardAction(
                icon = Icons.Filled.ThumbUp,
                label = if (hasVoted) "Voted" else "Vote",
                tint = if (hasVoted) Color.Gray else GoldMineColors.CharlotteGreen,
                enabled = !locked,
                onClick = onVote,
            )
            CardAction(
                icon = Icons.Filled.ThumbDown,
                label = if (hasNoVoted) "Voted No" else "Not Available",
                tint = if (hasNoVoted) Color.Gray else GoldMineColors.AccentRed,
                enabled = !locked,
                onClick = onNoVote,
            )

            Spacer(Modifier.weight(1f))

            when {
                event.votes >= FreebieEvent.VOTE_THRESHOLD -> StatusPill(
                    text = "Confirmed",
                    icon = Icons.Filled.CheckCircle,
                    contentColor = GoldMineColors.AccentGreen,
                    containerColor = GoldMineColors.AccentGreen.copy(alpha = 0.1f),
                )

                event.noVotes > 0 -> StatusPill(
                    text = "${event.noVotes}/${FreebieEvent.VOTE_THRESHOLD} No Votes",
                    icon = Icons.Filled.Warning,
                    contentColor = GoldMineColors.AccentOrange,
                    containerColor = GoldMineColors.AccentOrange.copy(alpha = 0.1f),
                )
            }
        }
    }
}

@Composable
private fun CardAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) tint else tint.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) tint else tint.copy(alpha = 0.5f),
        )
    }
}

private val CARD_DATE_FORMAT = SimpleDateFormat("EEEE, MMM d • h:mm a", Locale.US)
