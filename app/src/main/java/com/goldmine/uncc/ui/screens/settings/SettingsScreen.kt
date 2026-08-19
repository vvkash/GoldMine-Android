package com.goldmine.uncc.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goldmine.uncc.data.model.AppTickerType
import com.goldmine.uncc.ui.AppState
import com.goldmine.uncc.ui.AppViewModel
import com.goldmine.uncc.BuildConfig
import com.goldmine.uncc.ui.screens.home.FreebieIcon
import com.goldmine.uncc.ui.screens.home.homeButtonAccent
import com.goldmine.uncc.ui.screens.home.homeButtonIcon
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/** Settings list, ported from the iOS `SettingsView` (grouped list + disclosure groups). */
@Composable
fun SettingsScreen(
    appViewModel: AppViewModel,
    state: AppState,
    contentPadding: PaddingValues,
    onOpenPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    var editingName by remember { mutableStateOf(false) }
    var nameDraft by remember { mutableStateOf(state.userName) }
    var tickerExpanded by remember { mutableStateOf(false) }
    var notificationsExpanded by remember { mutableStateOf(false) }
    var buttonsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.userName) {
        if (!editingName) nameDraft = state.userName
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 12.dp),
            )
        }

        item { SectionHeader("Appearance") }
        item {
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.DarkMode,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "Dark Mode",
                    trailing = {
                        Switch(
                            checked = extras.isDark,
                            onCheckedChange = { appViewModel.setDarkMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = GoldMineColors.CharlotteGreen,
                            ),
                        )
                    },
                )
                if (state.darkModeOverride != null) {
                    RowDivider()
                    SettingsRow(
                        icon = Icons.Filled.Restore,
                        iconTint = GoldMineColors.AccentGray,
                        title = "Use System Appearance",
                        onClick = { appViewModel.useSystemAppearance() },
                    )
                }
            }
        }

        item { SectionHeader("Profile") }
        item {
            SettingsCard {
                if (editingName) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedTextField(
                            value = nameDraft,
                            onValueChange = { nameDraft = it },
                            singleLine = true,
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = {
                                    appViewModel.setUserName(nameDraft)
                                    editingName = false
                                },
                                enabled = nameDraft.isNotBlank(),
                            ) { Text("Save", color = GoldMineColors.CharlotteGreen) }
                            TextButton(
                                onClick = {
                                    nameDraft = state.userName
                                    editingName = false
                                },
                            ) { Text("Cancel", color = GoldMineColors.AccentRed) }
                        }
                    }
                } else {
                    SettingsRow(
                        icon = Icons.Filled.Edit,
                        iconTint = GoldMineColors.CharlotteGreen,
                        title = "Name",
                        value = state.userName.ifBlank { "Not set" },
                        onClick = { editingName = true },
                    )
                }
            }
        }

        item { SectionHeader("Home Screen") }
        item {
            SettingsCard {
                DisclosureRow(
                    icon = Icons.Filled.BarChart,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "Status Ticker",
                    expanded = tickerExpanded,
                    onToggle = { tickerExpanded = !tickerExpanded },
                )
                AnimatedVisibility(visible = tickerExpanded) {
                    Column {
                        AppTickerType.entries.forEach { type ->
                            RowDivider()
                            SettingsRow(
                                icon = tickerIcon(type),
                                iconTint = GoldMineColors.CharlotteGreen,
                                title = type.rawValue,
                                indent = true,
                                onClick = { appViewModel.setTickerType(type) },
                                trailing = {
                                    if (state.defaultTickerType == type) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = GoldMineColors.CharlotteGreen,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }

                RowDivider()

                DisclosureRow(
                    icon = Icons.Filled.Notifications,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "Notifications",
                    expanded = notificationsExpanded,
                    onToggle = { notificationsExpanded = !notificationsExpanded },
                )
                AnimatedVisibility(visible = notificationsExpanded) {
                    Column {
                        RowDivider()
                        ToggleRow(
                            icon = FreebieIcon,
                            iconTint = GoldMineColors.AccentBlue,
                            title = "Freebies",
                            checked = state.freebieNotificationsEnabled,
                            onCheckedChange = { appViewModel.setFreebieNotifications(it) },
                        )
                        RowDivider()
                        ToggleRow(
                            icon = Icons.Filled.Widgets,
                            iconTint = GoldMineColors.AccentOrange,
                            title = "Clubs & Organizations",
                            checked = state.clubsNotificationsEnabled,
                            onCheckedChange = { appViewModel.setClubsNotifications(it) },
                        )
                        RowDivider()
                        ToggleRow(
                            icon = Icons.Filled.Notifications,
                            iconTint = GoldMineColors.AccentPurple,
                            title = "Meet Ups",
                            checked = state.meetUpsNotificationsEnabled,
                            onCheckedChange = { appViewModel.setMeetUpsNotifications(it) },
                        )
                    }
                }

                RowDivider()

                DisclosureRow(
                    icon = Icons.Filled.Widgets,
                    iconTint = GoldMineColors.CharlotteGreen,
                    title = "Buttons",
                    expanded = buttonsExpanded,
                    onToggle = { buttonsExpanded = !buttonsExpanded },
                )
                AnimatedVisibility(visible = buttonsExpanded) {
                    Column {
                        state.homeButtons.sortedBy { it.order }.forEach { button ->
                            RowDivider()
                            ToggleRow(
                                icon = homeButtonIcon(button.id),
                                iconTint = homeButtonAccent(button.id),
                                title = button.name,
                                checked = button.isVisible,
                                onCheckedChange = { appViewModel.toggleHomeButton(button.id) },
                            )
                        }
                        RowDivider()
                        SettingsRow(
                            icon = Icons.Filled.Restore,
                            iconTint = GoldMineColors.AccentGray,
                            title = "Reset to Default",
                            indent = true,
                            onClick = { appViewModel.resetHomeButtons() },
                        )
                    }
                }
            }
        }

        item { SectionHeader("About") }
        item {
            SettingsCard {
                SettingsRow(
                    icon = null,
                    iconTint = Color.Unspecified,
                    title = "Version",
                    value = BuildConfig.VERSION_NAME,
                )
                RowDivider()
                SettingsRow(
                    icon = null,
                    iconTint = Color.Unspecified,
                    title = "Privacy Policy",
                    onClick = onOpenPrivacy,
                )
            }
        }
    }
}

private fun tickerIcon(type: AppTickerType): ImageVector = when (type) {
    AppTickerType.UREC_STATUS -> Icons.AutoMirrored.Filled.DirectionsRun
    AppTickerType.TODAYS_CLASSES -> Icons.Filled.CalendarMonth
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = LocalGoldMineColors.current.secondaryText,
        modifier = Modifier.padding(start = 32.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalGoldMineColors.current.cardBackground,
        ),
    ) {
        Column { content() }
    }
}

@Composable
private fun RowDivider() {
    Spacer(
        modifier = Modifier
            .padding(start = 52.dp)
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector?,
    iconTint: Color,
    title: String,
    value: String? = null,
    indent: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(start = if (indent) 32.dp else 16.dp, end = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(22.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalGoldMineColors.current.secondaryText,
            )
        }
        if (trailing != null) {
            trailing()
        } else if (onClick != null && value == null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GoldMineColors.AccentGray,
            )
        }
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsRow(
        icon = icon,
        iconTint = iconTint,
        title = title,
        indent = true,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = GoldMineColors.CharlotteGreen),
            )
        },
    )
}

@Composable
private fun DisclosureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    SettingsRow(
        icon = icon,
        iconTint = iconTint,
        title = title,
        onClick = onToggle,
        trailing = {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = GoldMineColors.AccentGray,
            )
        },
    )
}
