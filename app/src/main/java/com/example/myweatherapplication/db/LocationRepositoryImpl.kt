package com.example.myweatherapplication.db

import androidx.lifecycle.LiveData
import com.example.myweatherapplication.models.CityTown
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationDao:LocationDao):LocationRepository {
    override suspend fun insertLocation(location: CityTown) {
        locationDao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: CityTown) {
        locationDao.deleteLocation(location)
    }

    override fun getAllLocations(): LiveData<List<CityTown>> {
      return  locationDao.getAllLocations()
    }
}