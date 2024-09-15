package com.example.myweatherapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweatherapplication.db.LocationRepository
import com.example.myweatherapplication.models.Astro
import com.example.myweatherapplication.models.Condition
import com.example.myweatherapplication.models.ConditionX
import com.example.myweatherapplication.models.ConditionXX
import com.example.myweatherapplication.models.Current
import com.example.myweatherapplication.models.Day
import com.example.myweatherapplication.models.Forecast
import com.example.myweatherapplication.models.Forecastday
import com.example.myweatherapplication.models.Hour
import com.example.myweatherapplication.models.Location
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.data.repositories.WeatherReposotory
import com.example.myweatherapplication.data.weather.WeatherResponse
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response


class WeatherViewModelTest {



    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Mock
    lateinit var reposotory: WeatherReposotory

    @Mock
    lateinit var locRepository:LocationRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Deprecated("Deprecated in Java")
    @Before
     fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun get_weather_data() = runBlocking{
        val condition = Condition(2,"ll","ss")
        val crnt = Current(1,condition,.2,.3,.4,.5,5,1,"dd",2,.6,.7,.8,.9,1.1,1.2,1.3,1.4,1.5,6,"kk",.6,.9)
        val location = Location("kk",.3,"pp",2,.4,"mm","oo","jj")
        val astro = Astro("hh","aa","ww","hh","ss","hh")
       val conx = ConditionX(2,"j","l00")
        val conxx= ConditionXX(1,"g","k")
        val hour  = Hour(1,2,3,conxx,.2,.3,.5,.4,.5,.6,.7,.7,7,6,.7,.8,.9,.8,.8,.6,"jj",2,.3,.3,.5,3,3,4,"aa",.3,.4,.5,.5)
        val hour1 = Hour(1,2,3,conxx,.2,.3,.5,.4,.5,.6,.7,.7,7,6,.7,.8,.9,.8,.8,.6,"jj",2,.3,.3,.5,3,3,4,"aa",.3,.4,.5,.5)
        val day = Day(.1,.2,.3,.4,.5,conx,1,0,3,4,.5,.6,.7,.8,.9,.4,.5,.4,.5)
       val forecastDay = Forecastday(astro,"ll",1,day,arrayListOf(hour,hour1))
        val fore = Forecastday(astro,"ll",1,day,arrayListOf(hour,hour1))
       val forecast = Forecast(arrayListOf(forecastDay,fore))
        val resp = WeatherResponse(crnt,forecast,location )
        Mockito.`when`(reposotory.getForecastWeather(anyString(), anyString(), anyInt())).thenReturn(
            Response.success(resp))

     val sut = WeatherViewModel(reposotory,locRepository)
        sut.getResponse("chennai")
        testDispatcher.advanceUntilIdle()

        val res = sut.result.getOrAwaitValueTest()
        assertEquals(true,res is ScreenState.Success)
        assertEquals("mm",sut.result.value!!.data!!.location.name)


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getResponse with error response`() = runBlocking{
       // val errorMsg = "Some error message"
        val searchQuery = "your_search_query"


        // Mock the repository's getForecastWeather function to throw an exception
        Mockito.`when`(reposotory.getForecastWeather(anyString(), anyString(), anyInt())).
        thenReturn(Response.error(404,
            "some content".toResponseBody("plain/text".toMediaTypeOrNull())
        ))

        // Call the function you want to test
        val viewModel = WeatherViewModel(reposotory,locRepository)
        viewModel.getResponse(searchQuery)

        // Advance the dispatcher to execute coroutines
        testDispatcher.advanceUntilIdle()
        val result = viewModel.result.getOrAwaitValueTest()

        // Assert the LiveData values after the response
        assertEquals(true,result is ScreenState.Error )
        assertEquals("error",result.message)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getResponse with loading state`() = runBlocking {
        val searchQuery = "your_search_query"

        // Call the function you want to test
        val viewModel = WeatherViewModel(reposotory,locRepository)
        val testCoroutineScope = TestCoroutineScope(testDispatcher)
        testCoroutineScope.run {
            testDispatcher.pauseDispatcher()

            viewModel.getResponse(searchQuery)

            // Check if liveData is loading state or not.
        }
       testDispatcher.advanceUntilIdle()
     val result = viewModel.result.getOrAwaitValueTest()
        // No need to advance the dispatcher, as the loading state is set immediately

        // Assert the LiveData values after the response
        assertEquals(true,result is ScreenState.Loading)
    }












@OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }


}