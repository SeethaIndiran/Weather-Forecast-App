package com.example.myweatherapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "location_table")
data class CityTown (

        val name:String,
        @PrimaryKey(autoGenerate = true) val id:Int = 0
        ):Serializable