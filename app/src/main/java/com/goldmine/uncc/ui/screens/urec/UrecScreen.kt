package com.goldmine.uncc.ui.screens.urec

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldmine.uncc.data.model.GymOccupancy
import com.goldmine.uncc.data.model.busynessText
import com.goldmine.uncc.data.remote.GymOccupancyCalculator
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.components.GoldMineWebView
import com.goldmine.uncc.ui.navigation.WebDestination
import com.goldmine.uncc.ui.screens.home.OccupancyRing
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * UREC detail screen: the predicted-occupancy ring from the iOS `DetailedGymStatusView`,
 * followed by the live connect2mycloud head-count widget the iOS `GymStatusView` embeds.
 */
@Composable
fun UrecScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val extras = LocalGoldMineColors.current

    val occupancy by produceState(initialValue = GymOccupancyCalculator.occupancyAt()) {
        while (true) {
            value = GymOccupancyCalculator.occupancyAt()
            delay(60_000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
    ) {
        GoldMineHeader(title = "UREC Status", onBack = onBack, backLabel = "Done")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OccupancyRing(
                percentage = occupancy.occupancyPercentage,
                diameter = 150.dp,
                strokeWidth = 12.dp,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${occupancy.occupancyPercentage.roundToInt()}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = statusLabel(occupancy),
                        style = MaterialTheme.typography.titleSmall,
                        color = extras.secondaryText,
                    )
                }
            }

            Text(
                text = "Hours: 6:00 AM - 11:00 PM",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Live head count",
                style = MaterialTheme.typography.labelMedium,
                color = extras.secondaryText,
            )
        }

        GoldMineWebView(
            url = WebDestination.UREC_WIDGET.url,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            zoomScale = WebDestination.UREC_WIDGET.zoomScale,
        )
    }
}

private fun statusLabel(occupancy: GymOccupancy): String =
    if (occupancy.isClosed) "Closed" else busynessText(occupancy.occupancyPercentage)
