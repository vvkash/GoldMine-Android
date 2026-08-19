package com.goldmine.uncc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.goldmine.uncc.ui.AppState
import com.goldmine.uncc.ui.AppViewModel
import com.goldmine.uncc.ui.navigation.WebDestination
import com.goldmine.uncc.ui.screens.home.HomeScreen
import com.goldmine.uncc.ui.screens.settings.SettingsScreen
import com.goldmine.uncc.ui.screens.social.SocialScreen
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

private const val TAB_HOME = 0
private const val TAB_SOCIAL = 1
private const val TAB_SETTINGS = 2

/**
 * Tab host with the app's signature floating pill bar, ported from the iOS `MainTabView`.
 * The bar overlays the content, so each tab receives the pill height as bottom content padding.
 */
@Composable
fun MainTabScreen(
    appViewModel: AppViewModel,
    state: AppState,
    onOpenWeb: (WebDestination) -> Unit,
    onOpenUrec: () -> Unit,
    onOpenCampusMap: () -> Unit,
    onOpenClasses: () -> Unit,
    onOpenDiscounts: () -> Unit,
    onOpenDining: () -> Unit,
    onOpenPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(TAB_HOME) }
    val extras = LocalGoldMineColors.current

    val systemBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val systemTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val contentPadding = remember(systemBottom, systemTop) {
        PaddingValues(top = systemTop, bottom = systemBottom + 82.dp)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground),
    ) {
        when (selectedTab) {
            TAB_HOME -> HomeScreen(
                state = state,
                contentPadding = contentPadding,
                onOpenShortcut = { id ->
                    when (id) {
                        "gym" -> onOpenUrec()
                        "campusmap" -> onOpenCampusMap()
                        "classes" -> onOpenClasses()
                        "discounts" -> onOpenDiscounts()
                        "eats" -> onOpenDining()
                        "studyroom" -> onOpenWeb(WebDestination.STUDY_ROOM)
                        "events" -> onOpenWeb(WebDestination.EVENTS)
                        "parking" -> onOpenWeb(WebDestination.PARKING)
                        "bus" -> onOpenWeb(WebDestination.BUS)
                        "sports" -> onOpenWeb(WebDestination.SPORTS)
                    }
                },
                onOpenUrec = onOpenUrec,
                onOpenClasses = onOpenClasses,
            )

            TAB_SOCIAL -> SocialScreen(
                state = state,
                contentPadding = contentPadding,
                onOpenClubs = { onOpenWeb(WebDestination.CLUBS) },
            )

            else -> SettingsScreen(
                appViewModel = appViewModel,
                state = state,
                contentPadding = contentPadding,
                onOpenPrivacy = onOpenPrivacy,
            )
        }

        PillTabBar(
            selectedTab = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = systemBottom + 10.dp, start = 20.dp, end = 20.dp),
        )
    }
}

@Composable
private fun PillTabBar(
    selectedTab: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = CircleShape,
        color = if (extras.isDark) GoldMineColors.DarkCard else Color.White,
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PillTabItem(Icons.Filled.Home, "Home", selectedTab == TAB_HOME) { onSelect(TAB_HOME) }
            PillTabItem(Icons.Filled.Groups, "Social", selectedTab == TAB_SOCIAL) {
                onSelect(TAB_SOCIAL)
            }
            PillTabItem(Icons.Filled.Settings, "Settings", selectedTab == TAB_SETTINGS) {
                onSelect(TAB_SETTINGS)
            }
        }
    }
}

@Composable
private fun RowScope.PillTabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(if (selected) 1f else 0f)
                .clip(CircleShape)
                .background(GoldMineColors.NinerGold),
        )
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) GoldMineColors.CharlotteGreen else Color.Gray,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(24.dp),
        )
    }
}
