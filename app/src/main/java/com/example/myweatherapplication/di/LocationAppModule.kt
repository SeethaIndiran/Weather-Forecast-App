package com.example.myweatherapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myweatherapplication.util.Constants
import com.example.myweatherapplication.util.Constants.SHARED_PREFERENCES_NAME
import com.example.myweatherapplication.db.LocationDao
import com.example.myweatherapplication.db.LocationDatabase
import com.example.myweatherapplication.db.LocationRepository
import com.example.myweatherapplication.db.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationAppModule {

    @Singleton
    @Provides
    fun getLocationDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app,LocationDatabase::class.java, Constants.LOCATION_DATABASE)
            .build()



    @Singleton
    @Provides
    fun getLocationDao(db: LocationDatabase) = db.getLocationDao()


    @Singleton
    @Provides
    fun getLocationRepository(dao:LocationDao):LocationRepository{
        return LocationRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
}