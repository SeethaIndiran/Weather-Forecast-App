package com.example.myweatherapplication.di

import com.example.myweatherapplication.util.Constants
import com.example.myweatherapplication.data.weather.WeatherApiService
import com.example.myweatherapplication.data.repositories.WeatherRepositoryImpl
import com.example.myweatherapplication.data.repositories.WeatherReposotory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApiService): WeatherReposotory {
        return WeatherRepositoryImpl(api)
    }
}