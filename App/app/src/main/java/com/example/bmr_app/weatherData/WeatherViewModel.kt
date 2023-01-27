package com.example.bmr_app.weatherData

import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class WeatherViewModel(repository: weatherRepository) : ViewModel() {

    //liveData object connected to current weather
    private val jsonData: LiveData<weatherClass> = repository.data

    //second liveData object connected to entire database.
    private val avgTemp: LiveData<Double> = repository.avgTemp.asLiveData()

    //singleton repository for weather.
    private val mWeatherRepository: weatherRepository = repository

    //give repository location
    fun setLocation(lat: Double, lon: Double){
        mWeatherRepository.setLocation(lat, lon)
    }

    val data: LiveData<weatherClass>
        get() = jsonData

    val all: LiveData<Double>
        get() = avgTemp
}

class WeatherViewModelFactory(private val repository: weatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}