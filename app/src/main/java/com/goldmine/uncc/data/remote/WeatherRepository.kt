package com.goldmine.uncc.data.remote

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.goldmine.uncc.BuildConfig
import com.goldmine.uncc.data.local.goldMineDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Current conditions for the home header. */
data class WeatherSnapshot(
    val temperatureF: Int,
    val conditionId: Int,
    val condition: String,
)

/**
 * OpenWeather client for the UNC Charlotte campus coordinates.
 *
 * Mirrors the iOS `WeatherService`: a 30 minute refresh interval with the last successful
 * payload cached on disk so the header renders instantly (and offline).
 */
class WeatherRepository(context: Context) {

    private val appContext = context.applicationContext
    private val dataStore = appContext.goldMineDataStore

    private val json = Json { ignoreUnknownKeys = true }

    private val api: OpenWeatherApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenWeatherApi::class.java)
    }

    private object Keys {
        val CACHED_PAYLOAD = stringPreferencesKey("cachedWeatherData")
        val LAST_UPDATE = longPreferencesKey("lastWeatherUpdate")
    }

    suspend fun cachedSnapshot(): WeatherSnapshot? {
        val raw = dataStore.data.first()[Keys.CACHED_PAYLOAD] ?: return null
        return runCatching { json.decodeFromString<WeatherResponse>(raw).toSnapshot() }.getOrNull()
    }

    /**
     * Returns fresh conditions, or `null` when the cache is still warm / the request fails.
     * Pass [force] to bypass the refresh window.
     */
    suspend fun refresh(force: Boolean = false): WeatherSnapshot? {
        if (BuildConfig.OPENWEATHER_API_KEY.isBlank()) return null

        val lastUpdate = dataStore.data.first()[Keys.LAST_UPDATE] ?: 0L
        val now = System.currentTimeMillis()
        if (!force && lastUpdate != 0L && now - lastUpdate < REFRESH_INTERVAL_MS) return null

        return runCatching {
            val response = api.currentWeather(
                lat = CAMPUS_LAT,
                lon = CAMPUS_LON,
                apiKey = BuildConfig.OPENWEATHER_API_KEY,
            )
            dataStore.edit {
                it[Keys.CACHED_PAYLOAD] = json.encodeToString(response)
                it[Keys.LAST_UPDATE] = now
            }
            response.toSnapshot()
        }.onFailure { Log.w(TAG, "Weather refresh failed", it) }.getOrNull()
    }

    private fun WeatherResponse.toSnapshot(): WeatherSnapshot {
        val condition = weather.firstOrNull()
        return WeatherSnapshot(
            temperatureF = main.temp.toInt(),
            conditionId = condition?.id ?: CLEAR_SKY_ID,
            condition = condition?.main.orEmpty(),
        )
    }

    companion object {
        private const val TAG = "WeatherRepository"
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val CAMPUS_LAT = 35.3071
        private const val CAMPUS_LON = -80.7352
        private const val CLEAR_SKY_ID = 800
        val REFRESH_INTERVAL_MS = TimeUnit.MINUTES.toMillis(30)
    }
}
