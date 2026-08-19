package com.goldmine.uncc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goldmine.uncc.ui.AppViewModel
import com.goldmine.uncc.ui.GoldMineApp
import com.goldmine.uncc.ui.theme.GoldMineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val appViewModel: AppViewModel = viewModel(factory = AppViewModel.Factory)
            val state by appViewModel.state.collectAsStateWithLifecycle()
            val darkTheme = state.darkModeOverride ?: isSystemInDarkTheme()

            GoldMineTheme(darkTheme = darkTheme) {
                val focusManager = LocalFocusManager.current
                val keyboard = LocalSoftwareKeyboardController.current
                // Tapping outside a text field dismisses the keyboard, matching iOS.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                keyboard?.hide()
                            })
                        },
                ) {
                    GoldMineApp(appViewModel = appViewModel, state = state)
                }
            }
        }
    }
}
