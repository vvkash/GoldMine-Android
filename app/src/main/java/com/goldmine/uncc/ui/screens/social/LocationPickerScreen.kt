package com.goldmine.uncc.ui.screens.social

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goldmine.uncc.data.model.CampusBuildings
import com.goldmine.uncc.data.model.MapLocation
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.theme.GoldMineColors
import com.goldmine.uncc.ui.theme.LocalGoldMineColors
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private val CAMPUS = LatLng(35.3071, -80.7352)

/**
 * Map-based location picker. Tapping (or long-pressing) the map drops the pin and reverse
 * geocodes a readable name; the search box matches campus buildings first, then falls back to
 * the platform geocoder.
 */
@Composable
fun LocationPickerScreen(
    onCancel: () -> Unit,
    onConfirm: (MapLocation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CAMPUS, 15.5f)
    }

    val buildingMatches = remember(searchText) {
        if (searchText.isBlank()) {
            emptyList()
        } else {
            CampusBuildings.buildings.entries
                .filter { it.key.contains(searchText, ignoreCase = true) }
                .take(6)
        }
    }

    fun choose(point: LatLng, name: String?) {
        selected = point
        if (name != null) {
            locationName = name
        } else {
            scope.launch {
                locationName = reverseGeocode(context, point) ?: "Selected Location"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        GoldMineHeader(title = "Select Location", onBack = onCancel, backLabel = "Cancel")

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            singleLine = true,
            placeholder = { Text("Search locations") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        if (buildingMatches.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                items(buildingMatches) { entry ->
                    Text(
                        text = entry.key,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchText = ""
                                choose(entry.value, entry.key)
                                scope.launch {
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(entry.value, 17f)
                                }
                            }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
                onMapClick = { choose(it, null) },
                onMapLongClick = { choose(it, null) },
            ) {
                selected?.let { point ->
                    Marker(state = MarkerState(position = point), title = locationName)
                }
            }

            if (selected == null) {
                Text(
                    text = "Tap the map to select a location",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = locationName.ifBlank { "Selected Location" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Text(
                            text = "Tap confirm to use this location",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }

                    Spacer(Modifier.padding(horizontal = 6.dp))

                    Button(
                        onClick = {
                            val point = selected ?: return@Button
                            onConfirm(
                                MapLocation(
                                    title = locationName.ifBlank { "Selected Location" },
                                    latitude = point.latitude,
                                    longitude = point.longitude,
                                ),
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldMineColors.CharlotteGreen,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(
            Modifier.padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
        )
    }
}

@Suppress("DEPRECATION")
private suspend fun reverseGeocode(context: Context, point: LatLng): String? =
    withContext(Dispatchers.IO) {
        val nearestBuilding = CampusBuildings.buildings.entries.minByOrNull { (_, coordinate) ->
            val dLat = coordinate.latitude - point.latitude
            val dLng = coordinate.longitude - point.longitude
            dLat * dLat + dLng * dLng
        }
        // ~90 m radius: close enough to name the building rather than the street.
        if (nearestBuilding != null) {
            val dLat = nearestBuilding.value.latitude - point.latitude
            val dLng = nearestBuilding.value.longitude - point.longitude
            if (dLat * dLat + dLng * dLng < 0.0000008) return@withContext nearestBuilding.key
        }

        if (!Geocoder.isPresent()) return@withContext null
        runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            results?.firstOrNull()?.let { address ->
                address.featureName?.takeIf { it.isNotBlank() && it != address.thoroughfare }
                    ?: address.thoroughfare
                    ?: address.subLocality
                    ?: address.locality
            }
        }.getOrNull()
    }
