package com.goldmine.uncc.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

/**
 * [MarkerState] is mutable — the map writes drag and info-window state back into it — so it has to
 * survive recomposition. This keeps one instance per coordinate and swaps it when the pin moves.
 */
@Composable
fun rememberMarkerStateAt(position: LatLng): MarkerState =
    remember(position.latitude, position.longitude) { MarkerState(position = position) }
