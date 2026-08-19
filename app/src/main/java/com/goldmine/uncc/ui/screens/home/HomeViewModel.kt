package com.goldmine.uncc.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.goldmine.uncc.data.model.GymOccupancy
import com.goldmine.uncc.data.remote.GymOccupancyCalculator
import com.goldmine.uncc.data.remote.WeatherRepository
import com.goldmine.uncc.data.remote.WeatherSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class HomeUiState(
    val weather: WeatherSnapshot? = null,
    val occupancy: GymOccupancy = GymOccupancyCalculator.occupancyAt(),
)

/** Drives the home header (weather) and the UREC ticker, refreshing both on a timer. */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            weatherRepository.cachedSnapshot()?.let { cached ->
                _uiState.update { it.copy(weather = cached) }
            }
            while (isActive) {
                weatherRepository.refresh()?.let { fresh ->
                    _uiState.update { it.copy(weather = fresh) }
                }
                delay(WeatherRepository.REFRESH_INTERVAL_MS)
            }
        }

        viewModelScope.launch {
            while (isActive) {
                _uiState.update { it.copy(occupancy = GymOccupancyCalculator.occupancyAt()) }
                delay(OCCUPANCY_REFRESH_MS)
            }
        }
    }

    fun refreshWeather() = viewModelScope.launch {
        weatherRepository.refresh(force = true)?.let { fresh ->
            _uiState.update { it.copy(weather = fresh) }
        }
    }

    companion object {
        private val OCCUPANCY_REFRESH_MS = TimeUnit.MINUTES.toMillis(5)

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY],
                )
                return HomeViewModel(application) as T
            }
        }
    }
}
