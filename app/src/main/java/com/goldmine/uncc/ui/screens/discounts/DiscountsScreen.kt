package com.goldmine.uncc.ui.screens.discounts

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.goldmine.uncc.data.model.Discount
import com.goldmine.uncc.ui.components.CircleIcon
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/**
 * Native student-discount directory.
 *
 * The iOS app only opened studentbeans.com in a web view; the catalogue that ships in
 * `Discount.samples` was never surfaced. Android renders it natively (each row deep-links to
 * the offer) with Student Beans still available at the bottom.
 */
@Composable
fun DiscountsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val extras = LocalGoldMineColors.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Discounts", onBack = onBack, backLabel = "Done")

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(Discount.samples, key = Discount::name) { discount ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, discount.websiteUrl.toUri()),
                                )
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = extras.cardBackground),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircleIcon(
                            icon = Icons.Filled.LocalOffer,
                            tint = GoldMineColors.AccentGreen,
                            diameter = 40.dp,
                            ringWidth = 2.dp,
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp),
                        ) {
                            Text(
                                text = discount.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = discount.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = extras.secondaryText,
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = GoldMineColors.AccentGray,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
