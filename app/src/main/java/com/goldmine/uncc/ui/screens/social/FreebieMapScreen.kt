package com.goldmine.uncc.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.goldmine.uncc.data.model.FreebieEvent
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val CAMPUS_CENTER = LatLng(35.3071, -80.7352)

/** Full-screen map of every active freebie — the iOS `EnergyDrinkMapView`. */
@Composable
fun FreebieMapScreen(
    events: List<FreebieEvent>,
    title: String = "Freebie Map",
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val focus = events.firstOrNull()?.let { LatLng(it.location.latitude, it.location.longitude) }
        ?: CAMPUS_CENTER
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(focus, if (events.size == 1) 17f else 15f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = title, onBack = onBack, backLabel = "Done")

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
            ) {
                events.forEach { event ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(event.location.latitude, event.location.longitude),
                        ),
                        title = event.company,
                        snippet = event.location.title,
                    )
                }
            }
        }
    }
}
