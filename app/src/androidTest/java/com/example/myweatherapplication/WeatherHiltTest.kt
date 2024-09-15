package com.example.myweatherapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweatherapplication.db.LocationDao
import com.example.myweatherapplication.db.LocationDatabase
import com.example.myweatherapplication.models.CityTown
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class WeatherHiltTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var locDB: LocationDatabase
    private lateinit var locDAO: LocationDao

    @Before
    fun setUp() {
        hiltRule.inject()
        locDAO = locDB.getLocationDao()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation() = runBlockingTest {
        val loc = CityTown("chennai", 1)
        locDAO.insertLocation(loc)

        val getAllLocs = locDAO.getAllLocations().getOrAwaitValue()
        Assert.assertEquals(1, getAllLocs.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteLocation() = runBlockingTest {
        val loc = CityTown("chennai", 1)
        locDAO.insertLocation(loc)
        locDAO.deleteLocation(loc)

        val getAllLocs = locDAO.getAllLocations().getOrAwaitValue()
        Assert.assertEquals(0, getAllLocs.size)
    }

    @After
    fun teatDown(){
        locDB.close()
    }
}