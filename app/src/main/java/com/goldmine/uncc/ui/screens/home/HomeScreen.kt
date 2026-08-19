package com.goldmine.uncc.ui.screens.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goldmine.uncc.data.model.AppTickerType
import com.goldmine.uncc.data.model.HomeButton
import com.goldmine.uncc.data.model.classesOn
import com.goldmine.uncc.ui.AppState
import com.goldmine.uncc.ui.components.NavigationButton
import java.time.LocalDate

/**
 * The landing screen: greeting + live weather, a configurable status ticker, and the
 * user-ordered shortcut grid. Ported from the iOS `MainHomeView`.
 */
@Composable
fun HomeScreen(
    state: AppState,
    contentPadding: PaddingValues,
    onOpenShortcut: (String) -> Unit,
    onOpenUrec: () -> Unit,
    onOpenClasses: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val visibleButtons = state.homeButtons.filter { it.isVisible }.sortedBy { it.order }

    LaunchedEffect(Unit) { homeViewModel.refreshWeather() }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(Modifier.height(contentPadding.calculateTopPadding()))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Hi, ${state.userName}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )

            homeState.weather?.let { weather ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = weatherIcon(weather.conditionId),
                        contentDescription = weather.condition,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        text = "${weather.temperatureF}°",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        when (state.defaultTickerType) {
            AppTickerType.UREC_STATUS -> GymStatusBadge(
                occupancy = homeState.occupancy,
                onClick = onOpenUrec,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            AppTickerType.TODAYS_CLASSES -> TodaysClassesBadge(
                classes = state.classes.classesOn(LocalDate.now()),
                onClick = onOpenClasses,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(visibleButtons, key = HomeButton::id) { button ->
                NavigationButton(
                    title = button.name,
                    icon = homeButtonIcon(button.id),
                    accent = homeButtonAccent(button.id),
                    onClick = { onOpenShortcut(button.id) },
                )
            }
        }
    }
}
