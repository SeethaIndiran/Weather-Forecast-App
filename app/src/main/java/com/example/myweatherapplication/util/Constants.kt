package com.example.myweatherapplication.util

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object Constants {
    const val BASE_URL = "http://api.weatherapi.com/"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val LOCATION_DATABASE = "location_database"
    const val SHARED_PREFERENCES_NAME = "sharedPref"
   // const val CURRENT_LOCATION_FIRST_TIME = "current_location"
    const val CURRENT_LOCATION = "current_location"
    const val SWITCH_TOGGLE = "switch_toggle"
    const val CURNT_LOC_NAME = "crnt_loc_name"
    const val LOC_NAME = "loc_name"
    const val TEMP_UNIT = "temp_unit"
    const val WIND_VALUE = "wind_value"
    const val PRECIPITATION_VALUE = "preci_value"
    const val PRESSURE_VALUE = "pressure_value"
    const val VISIBILITY_VALUE = "visibility_value"

   // const val  LOCATION_UPDATE_INTERVAL =5000L
   // const val  FASTEST_LOCATION_INTERVAL = 2000L


   // val retrofit:Retrofit by lazy {
   //    Retrofit.Builder().baseUrl(BASE_URL)
     //       .addConverterFactory(GsonConverterFactory.create()).build()
  //  }
   // val apiService:WeatherApiService by lazy {
     //   retrofit.create(WeatherApiService::class.java)
   // }

    fun hasLocationPermissions(context: Context) =

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        }else{
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }



}