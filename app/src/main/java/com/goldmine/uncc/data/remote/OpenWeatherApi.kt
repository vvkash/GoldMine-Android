package com.goldmine.uncc.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class WeatherResponse(
    val main: MainWeather,
    val weather: List<WeatherCondition> = emptyList(),
) {
    @Serializable
    data class MainWeather(
        val temp: Double,
        @SerialName("feels_like") val feelsLike: Double = temp,
        @SerialName("temp_min") val tempMin: Double = temp,
        @SerialName("temp_max") val tempMax: Double = temp,
        val humidity: Int = 0,
    )

    @Serializable
    data class WeatherCondition(
        val id: Int,
        val main: String = "",
        val description: String = "",
        val icon: String = "",
    )
}

interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun currentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial",
    ): WeatherResponse
}
