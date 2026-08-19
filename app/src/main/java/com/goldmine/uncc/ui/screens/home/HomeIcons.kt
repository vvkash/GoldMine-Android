package com.goldmine.uncc.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.goldmine.uncc.data.model.HomeButton
import com.goldmine.uncc.ui.theme.GoldMineColors

/** SF Symbol → Material icon mapping for the home shortcuts. */
fun homeButtonIcon(id: String): ImageVector = when (id) {
    HomeButton.GYM -> Icons.AutoMirrored.Filled.DirectionsRun
    HomeButton.STUDY_ROOM -> Icons.AutoMirrored.Filled.MenuBook
    HomeButton.EVENTS -> Icons.Filled.Event
    HomeButton.PARKING -> Icons.Filled.DirectionsCar
    HomeButton.BUS -> Icons.Filled.DirectionsBus
    HomeButton.DISCOUNTS -> Icons.Filled.LocalOffer
    HomeButton.CLASSES -> Icons.Filled.School
    HomeButton.CAMPUS_MAP -> Icons.Filled.Map
    HomeButton.EATS -> Icons.Filled.Restaurant
    HomeButton.SPORTS -> Icons.Filled.SportsBasketball
    else -> Icons.Filled.Apartment
}

/** Accent colours copied from the iOS `NavigationButton` call sites. */
fun homeButtonAccent(id: String): Color = when (id) {
    HomeButton.GYM -> GoldMineColors.AccentGreen
    HomeButton.CAMPUS_MAP -> GoldMineColors.AccentPurple
    HomeButton.STUDY_ROOM -> GoldMineColors.AccentRed
    HomeButton.EVENTS -> GoldMineColors.AccentPurple
    HomeButton.SPORTS -> GoldMineColors.AccentOrange
    HomeButton.PARKING -> GoldMineColors.AccentBlue
    HomeButton.BUS -> GoldMineColors.AccentYellow
    HomeButton.DISCOUNTS -> GoldMineColors.AccentGreen
    HomeButton.CLASSES -> GoldMineColors.AccentOrange
    HomeButton.EATS -> GoldMineColors.AccentYellow
    else -> GoldMineColors.CharlotteGreen
}

/** OpenWeather condition id → icon, mirroring `WeatherService.getWeatherIcon(for:)`. */
fun weatherIcon(conditionId: Int): ImageVector = when (conditionId) {
    in 200..232 -> Icons.Filled.Bolt
    in 300..321 -> Icons.Filled.Grain
    in 500..531 -> Icons.Filled.WaterDrop
    in 600..622 -> Icons.Filled.AcUnit
    in 701..781 -> Icons.Filled.BlurOn
    800 -> Icons.Filled.WbSunny
    in 801..804 -> Icons.Filled.Cloud
    else -> Icons.Filled.WbSunny
}

val FreebieIcon: ImageVector = Icons.Filled.CardGiftcard
