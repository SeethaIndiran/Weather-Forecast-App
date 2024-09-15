package com.example.myweatherapplication

import com.example.myweatherapplication.data.weather.WeatherApiService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherAPITest {


    lateinit var mockWebServer: MockWebServer
    lateinit var weatherAPI: WeatherApiService

    @Before
    fun setUp(){
        mockWebServer = MockWebServer()
        val gson = GsonBuilder().setLenient().create()
        weatherAPI = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(WeatherApiService::class.java)
    }

    @Test
    fun getWeatherResponseTest() = runBlocking {
                val mockResponse = MockResponse()
               mockResponse.setBody("{}")
        mockWebServer.enqueue(mockResponse)

        val response = weatherAPI.getForecast("","",2)
        mockWebServer.takeRequest()
       // val num = 0

        Assert.assertEquals(true, response.body().toString().isNotEmpty())
    }

    @Test
    fun getWeatherResponseTestReturns() = runBlocking {
        val mockResponse = MockResponse()
        val content = Helper.readFileString("/response.json")
        mockResponse.setResponseCode(200)
        mockResponse.setBody(content)
        mockWebServer.enqueue(mockResponse)

        val response = weatherAPI.getForecast("123","london",2)
        mockWebServer.takeRequest()
      //  val num = 0

        Assert.assertEquals(true, response.body().toString().isNotEmpty())
    }

    @Test
    fun getWeatherResponseTestReturnsError() = runBlocking {
        val mockResponse = MockResponse()
      //  val content = Helper.readFileString("/response.json")
        mockResponse.setResponseCode(404)
        mockResponse.setBody("error")
        mockWebServer.enqueue(mockResponse)

        val response = weatherAPI.getForecast("123","london",2)
        mockWebServer.takeRequest()
     //   val num = 0

        Assert.assertEquals(false, response.isSuccessful)
        Assert.assertEquals(404,response.code())
    }

    @After
    fun tearDown(){
        mockWebServer.shutdown()
    }
}