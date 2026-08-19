package com.goldmine.uncc.ui.screens.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goldmine.uncc.R
import com.goldmine.uncc.data.firebase.FreebieFeed
import com.goldmine.uncc.data.model.FreebieEvent
import com.goldmine.uncc.ui.AppState
import com.goldmine.uncc.ui.components.CenteredMessage
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.components.HeaderIconButton
import com.goldmine.uncc.ui.components.SocialButton
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

private enum class SocialSection { HUB, FREEBIES, MEET_UPS, CLUBS }

/** Social hub with the Freebies feed plus the Meet Ups / Clubs placeholders. */
@Composable
fun SocialScreen(
    state: AppState,
    contentPadding: PaddingValues,
    onOpenClubs: () -> Unit,
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel = viewModel(factory = SocialViewModel.Factory),
) {
    var section by remember { mutableStateOf(SocialSection.HUB) }

    when (section) {
        SocialSection.HUB -> SocialHub(
            contentPadding = contentPadding,
            onSelect = { section = it },
            modifier = modifier,
        )

        SocialSection.FREEBIES -> FreebiesSection(
            state = state,
            contentPadding = contentPadding,
            viewModel = socialViewModel,
            onBack = { section = SocialSection.HUB },
            modifier = modifier,
        )

        SocialSection.MEET_UPS -> ComingSoonSection(
            title = "Meet Ups",
            icon = Icons.Filled.EventAvailable,
            iconTint = GoldMineColors.AccentPurple,
            heading = "Meet Ups Coming Soon!",
            message = "This feature is under development. Soon you'll be able to discover and " +
                "create student meetups around campus.",
            contentPadding = contentPadding,
            onBack = { section = SocialSection.HUB },
            modifier = modifier,
        )

        SocialSection.CLUBS -> ComingSoonSection(
            title = "Clubs & Organizations",
            icon = Icons.Filled.Apartment,
            iconTint = GoldMineColors.AccentOrange,
            heading = "Clubs Directory Coming Soon!",
            message = "This feature will connect you with over 200+ student organizations at " +
                "UNC Charlotte. Check back soon for the full directory.",
            contentPadding = contentPadding,
            onBack = { section = SocialSection.HUB },
            actionLabel = "Visit UNC Charlotte Clubs Website",
            onAction = onOpenClubs,
            modifier = modifier,
        )
    }
}

@Composable
private fun SocialHub(
    contentPadding: PaddingValues,
    onSelect: (SocialSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = contentPadding.calculateTopPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.uncc_home),
            contentDescription = "UNC Charlotte campus",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(3.dp, GoldMineColors.CharlotteGreen, RoundedCornerShape(15.dp)),
        )

        Text(
            text = "Social Events",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SocialButton(
                icon = Icons.Filled.CardGiftcard,
                title = "Freebies",
                accent = GoldMineColors.AccentBlue,
                onClick = { onSelect(SocialSection.FREEBIES) },
            )
            SocialButton(
                icon = Icons.Filled.Groups,
                title = "Meet Ups",
                accent = GoldMineColors.AccentPurple,
                onClick = { onSelect(SocialSection.MEET_UPS) },
            )
            SocialButton(
                icon = Icons.Filled.Apartment,
                title = "Clubs & Organizations",
                accent = GoldMineColors.AccentOrange,
                onClick = { onSelect(SocialSection.CLUBS) },
            )
        }
    }
}

@Composable
private fun FreebiesSection(
    state: AppState,
    contentPadding: PaddingValues,
    viewModel: SocialViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val feed by viewModel.feed.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showingAdd by remember { mutableStateOf(false) }
    var mapEvents by remember { mutableStateOf<List<FreebieEvent>?>(null) }

    val events = (feed as? FreebieFeed.Success)?.events.orEmpty()

    if (showingAdd) {
        AddFreebieScreen(
            isSubmitting = isSubmitting,
            onCancel = { showingAdd = false },
            onSubmit = { event -> viewModel.addEvent(event) { showingAdd = false } },
            modifier = modifier,
        )
        return
    }

    mapEvents?.let { shown ->
        FreebieMapScreen(
            events = shown,
            title = if (shown.size == 1) shown.first().company else "Freebie Map",
            onBack = { mapEvents = null },
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        GoldMineHeader(
            title = "Freebies",
            onBack = onBack,
            trailing = {
                HeaderIconButton(
                    icon = Icons.Filled.Map,
                    contentDescription = "Freebie map",
                    onClick = { mapEvents = events },
                )
            },
        )

        when {
            feed is FreebieFeed.Unavailable -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CenteredMessage(
                    icon = Icons.Filled.CardGiftcard,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "Freebies unavailable",
                    message = "Add your google-services.json to enable the live campus feed.",
                )
            }

            feed is FreebieFeed.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CircularProgressIndicator(color = GoldMineColors.CharlotteGreen)
                    Text(
                        text = "Loading freebies...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = extras.secondaryText,
                    )
                }
            }

            events.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CenteredMessage(
                    icon = Icons.Filled.CardGiftcard,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "No freebies",
                    message = (feed as? FreebieFeed.Failure)?.message
                        ?: "Report freebies you see on campus",
                    action = {
                        Button(
                            onClick = { showingAdd = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldMineColors.CharlotteGreen,
                                contentColor = Color.White,
                            ),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Text(
                                text = "Report Freebie",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    },
                )
            }

            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(events, key = { it.documentId ?: it.id }) { event ->
                    FreebieCard(
                        event = event,
                        userName = state.userName,
                        onVote = {
                            viewModel.vote(
                                event = event,
                                userName = state.userName,
                                notificationsEnabled = state.freebieNotificationsEnabled,
                            )
                        },
                        onNoVote = { viewModel.noVote(event, state.userName) },
                        onOpenMap = { mapEvents = listOf(event) },
                    )
                }
            }
        }

        if (events.isNotEmpty()) {
            Button(
                onClick = { showingAdd = true },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldMineColors.CharlotteGreen,
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp),
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Report Freebie",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.labelMedium,
                color = GoldMineColors.AccentRed,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.dismissError() }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        Spacer(Modifier.height(contentPadding.calculateBottomPadding()))
    }
}

@Composable
private fun ComingSoonSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    heading: String,
    message: String,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val extras = LocalGoldMineColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        GoldMineHeader(title = title, onBack = onBack)

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            CenteredMessage(
                icon = icon,
                iconTint = iconTint,
                title = heading,
                message = message,
                action = {
                    OutlinedButton(
                        onClick = { onAction?.invoke() },
                        enabled = onAction != null,
                        shape = RoundedCornerShape(25.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            GoldMineColors.CharlotteGreen,
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GoldMineColors.CharlotteGreen,
                            disabledContentColor = GoldMineColors.CharlotteGreen,
                        ),
                    ) {
                        Text(
                            text = actionLabel ?: "Notify Me When Available",
                            fontWeight = FontWeight.Medium,
                        )
                        if (actionLabel != null) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(18.dp),
                            )
                        }
                    }
                },
            )
        }

        Spacer(Modifier.height(contentPadding.calculateBottomPadding()))
    }
}
