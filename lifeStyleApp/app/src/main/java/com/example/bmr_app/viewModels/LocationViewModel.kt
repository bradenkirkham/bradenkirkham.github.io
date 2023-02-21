package com.example.bmr_app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel: ViewModel() {

    private val _Latitude = MutableLiveData<Double>(0.0)
    val latitude : LiveData<Double> = _Latitude
    private val _Longitude = MutableLiveData<Double>(0.0)
    val longitude : LiveData<Double> = _Longitude

    private val _step = MutableLiveData<String>()
    val step: LiveData<String> = _step

    fun setStep(step:String){
        _step.value = step
    }
    fun getStep_(): String? {
        if(step.value == null){
            return "0"
        }

        return step.value
    }

    fun setlatitude(latitude: Double){
        _Latitude.value = latitude
    }
    fun setlongitude(longitude: Double){
        _Longitude.value = longitude
    }
    fun returnLatitude(): Double? {
        return latitude.value
    }
    fun returnLogitude(): Double?{
        return longitude.value
    }



}