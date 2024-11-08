package com.example.myweatherapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myweatherapplication.models.CityTown

@Database(entities = [CityTown::class], version = 1, exportSchema = false)
abstract class LocationDatabase:RoomDatabase() {

    abstract fun getLocationDao():LocationDao
}