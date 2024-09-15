package com.example.myweatherapplication

import android.content.Context
import androidx.room.Room
import com.example.myweatherapplication.db.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    @Singleton
    fun getLocationDatabase(@ApplicationContext app: Context) =
        Room.inMemoryDatabaseBuilder(app, LocationDatabase::class.java).allowMainThreadQueries()
            .build()

}