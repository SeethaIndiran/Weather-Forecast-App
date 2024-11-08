package com.example.myweatherapplication.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.example.myweatherapplication.BuildConfig
import com.example.myweatherapplication.util.Constants
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.data.repositories.WeatherReposotory
import com.example.myweatherapplication.data.weather.WeatherResponse
import com.example.myweatherapplication.db.LocationRepository
import com.example.myweatherapplication.models.CityTown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(val reposotory: WeatherReposotory, val locationRepository: LocationRepository):ViewModel() {

     var _result = MutableLiveData<ScreenState<WeatherResponse>>()
    var result  : LiveData<ScreenState<WeatherResponse>> = _result

     val _isCelsius = MutableLiveData<Boolean>()
    val isCelsius: LiveData<Boolean> get() = _isCelsius

    val _windValue = MutableLiveData<String>()
    val windValue: LiveData<String> get() = _windValue

    val _preciValue = MutableLiveData<String>()
    val preciValue: LiveData<String> get() = _preciValue

    val _pressureValue = MutableLiveData<String>()
    val pressureValue: LiveData<String> get() = _pressureValue

    val _visValue = MutableLiveData<String>()
    val visValue: LiveData<String> get() = _visValue

   // var results = MediatorLiveData<WeatherResponse>()

    var days = 5



    fun toggleTemp(boolean:Boolean){
        _isCelsius.value = boolean
    }

    fun windValue(value:String){
        _windValue.value = value
    }


    fun precipitationValue(value:String){
        _preciValue.value = value
    }


    fun pressureValue(value:String){
        _pressureValue.value = value
    }


    fun visibilityValue(value:String){
        _visValue.value = value
    }




    @SuppressLint("SuspiciousIndentation")
    fun getResponse(searchQuery:String) {

        _result.postValue(ScreenState.Loading(null))

        viewModelScope.launch {

          //  try{


           val weatherResult = reposotory.getForecastWeather(BuildConfig.API_KEY, searchQuery, days)


              if(  weatherResult.isSuccessful) {
                  if(weatherResult.body()!=null){

                  _result.postValue(ScreenState.Success(weatherResult.body()!!))
             //     Log.i("result", "{${_result}}")

              } else{
                      _result.postValue((ScreenState.Error("error")))
                  }
              }
              else{
                  _result.postValue((ScreenState.Error("error")))
              }
            }

        }



    fun insertLocation(location: CityTown) = viewModelScope.launch {
        locationRepository.insertLocation(location)
    }


    fun deleteLocation(location: CityTown) = viewModelScope.launch {
        locationRepository.deleteLocation(location)
    }

    fun getLocations():LiveData<List<CityTown>> =
      locationRepository.getAllLocations()


}








