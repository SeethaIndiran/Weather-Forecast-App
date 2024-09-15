package com.example.myweatherapplication.data.weather

import com.example.myweatherapplication.models.Current
import com.example.myweatherapplication.models.Forecast
import com.example.myweatherapplication.models.Location
import java.io.Serializable

data class WeatherResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
):Serializable