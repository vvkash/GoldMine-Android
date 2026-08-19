package com.goldmine.uncc.ui.screens.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.goldmine.uncc.ui.components.GoldMineHeader
import com.goldmine.uncc.ui.components.GoldMineWebView
import com.goldmine.uncc.ui.navigation.WebDestination
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/** Generic host for the campus web resources (parking, bus, events, sports, dining…). */
@Composable
fun WebScreen(
    destination: WebDestination,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = LocalGoldMineColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
    ) {
        GoldMineHeader(title = destination.title, onBack = onBack, backLabel = "Done")
        GoldMineWebView(
            url = destination.url,
            modifier = Modifier.weight(1f),
            zoomScale = destination.zoomScale,
            onCannotGoBack = onBack,
        )
    }
}
