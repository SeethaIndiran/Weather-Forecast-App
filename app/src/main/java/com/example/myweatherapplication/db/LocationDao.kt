package com.example.myweatherapplication.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.myweatherapplication.models.CityTown

@Dao
interface LocationDao {

    @Insert
    suspend fun insertLocation( location: CityTown)

    @Delete
    suspend fun deleteLocation(location: CityTown)

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): LiveData<List<CityTown>>
}