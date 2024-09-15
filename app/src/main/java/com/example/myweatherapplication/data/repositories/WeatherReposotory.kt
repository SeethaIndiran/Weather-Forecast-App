package com.example.myweatherapplication.data.repositories

import com.example.myweatherapplication.data.weather.WeatherResponse
import retrofit2.Response

interface WeatherReposotory {

    suspend fun getForecastWeather( apiKey:String,city:String,  days:Int) :Response<WeatherResponse>

}