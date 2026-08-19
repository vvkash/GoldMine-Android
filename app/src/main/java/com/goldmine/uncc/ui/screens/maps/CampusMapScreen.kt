package com.goldmine.uncc.ui.screens.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.goldmine.uncc.data.model.CampusMapBuildings
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.components.HeaderIconButton
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/** Full campus map with every building pin — the iOS `CampusMapView`. */
@Composable
fun CampusMapScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val extras = LocalGoldMineColors.current
    val context = LocalContext.current

    var showUserLocation by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result -> showUserLocation = result.values.any { it } }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CampusMapBuildings.center, 15.5f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(
            title = "Campus Map",
            onBack = onBack,
            trailing = {
                HeaderIconButton(
                    icon = if (showUserLocation) {
                        Icons.Filled.MyLocation
                    } else {
                        Icons.Filled.LocationOn
                    },
                    contentDescription = "My location",
                    onClick = {
                        if (showUserLocation) {
                            showUserLocation = false
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
                )
            },
        )

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = showUserLocation),
                uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
            ) {
                CampusMapBuildings.all.forEach { (name, position) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = name,
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN,
                        ),
                    )
                }
            }
        }
    }
}
