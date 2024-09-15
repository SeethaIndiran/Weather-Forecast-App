package com.example.myweatherapplication.db

import androidx.lifecycle.LiveData
import com.example.myweatherapplication.models.CityTown

interface LocationRepository {

    suspend fun insertLocation(location: CityTown)

    suspend fun deleteLocation(location: CityTown)

    fun getAllLocations(): LiveData<List<CityTown>>
}