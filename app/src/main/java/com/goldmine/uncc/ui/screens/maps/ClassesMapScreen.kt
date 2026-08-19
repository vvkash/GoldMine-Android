package com.goldmine.uncc.ui.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.goldmine.uncc.core.colorFromHex
import com.goldmine.uncc.core.formatMinutesOfDay
import com.goldmine.uncc.data.model.CampusBuildings
import com.goldmine.uncc.data.model.ClassItem
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.components.rememberMarkerStateAt
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

/** Pins every scheduled class on the campus map — the iOS `ClassesMapView`. */
@Composable
fun ClassesMapScreen(
    classes: List<ClassItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val located = classes.mapNotNull { item ->
        CampusBuildings.coordinateFor(item.buildingName)?.let { item to it }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            located.firstOrNull()?.second ?: com.goldmine.uncc.data.model.CampusMapBuildings.center,
            15.5f,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Class Locations", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
            ) {
                located.forEach { (item, position) ->
                    key(item.id) {
                        Marker(
                            state = rememberMarkerStateAt(position),
                            title = item.name,
                            snippet = "${item.buildingName} ${item.roomNumber} · " +
                                "${formatMinutesOfDay(item.startMinutes)}-" +
                                formatMinutesOfDay(item.endMinutes),
                            icon = BitmapDescriptorFactory.defaultMarker(hueOf(item.color)),
                        )
                    }
                }
            }
        }
    }
}

private fun hueOf(hex: String): Float {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(colorFromHex(hex).toArgb(), hsl)
    return hsl[0]
}
