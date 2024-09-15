package com.example.myweatherapplication.data.repositories

import com.example.myweatherapplication.data.weather.WeatherApiService
import com.example.myweatherapplication.data.weather.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val api: WeatherApiService):
    WeatherReposotory {
    override suspend fun getForecastWeather(
        apiKey: String,
        city: String,
        days: Int
    ): Response<WeatherResponse> {
        return api.getForecast(apiKey, city, days)
    }
}