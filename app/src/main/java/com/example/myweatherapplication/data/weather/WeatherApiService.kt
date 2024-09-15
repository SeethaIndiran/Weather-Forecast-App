package com.example.myweatherapplication.data.weather

import com.example.myweatherapplication.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast.json")
   suspend fun getForecast(
        @Query("Key") apiKey:String = Constants.API_KEY,
        @Query("q") city:String,
        @Query("days") days:Int
    ): Response<WeatherResponse>

}