package com.example.bmr_app.weatherData

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import androidx.annotation.WorkerThread
import com.example.bmr_app.MainActivity

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized



class weatherRepository private constructor(private val weatherApplication: weatherApplication, coroutineScope: CoroutineScope){
    val data = MutableLiveData<weatherClass>()
    val avgTemp: Flow<Double> = weatherDatabase.getDatabase(weatherApplication.applicationContext, coroutineScope).weatherDao().getAvgTemp()


    private var mLat: Double? = null
    private var mLon: Double? = null

    //Json string is the data that comes back from the api request
    private var mJsonString: String? = null

    //we need the weather dao to do inserts into database (how the repository connects to database)
//    private var mWeatherDao: weatherDao = weatherDatabase.getDatabase(weatherApplication.applicationContext, coroutineScope).weatherDao()

    fun setLocation(lat: Double, lon: Double){
        //storing location in member variable
        mLat = lat
        mLon = lon

        mScope.launch(Dispatchers.IO){

            fetchAndParseWeatherData(mLat!!, mLon!!)

            if(mJsonString!=null) {
                //not sure if the whole gson from json stuff here is needed since i'm currently just doing room?
                var weatherdata = (Gson()).fromJson(mJsonString, weatherClass::class.java)

                mJsonString = weatherdata.toString()

                Log.d("temp", (weatherdata.main.temp - 273.15).toString())
                var weathertable = weatherTable(id = weatherdata.id, city = weatherdata.name, lat = weatherdata.coord.lat, lon = weatherdata.coord.lon,
                    temp = (weatherdata.main.temp - 273.15), humidity = weatherdata.main.humidity, pressure = weatherdata.main.pressure, wind = weatherdata.wind.speed,
                    desc = weatherdata.weather[0].description, main = weatherdata.weather[0].main)

                Log.d("weathertable", weathertable.temp.toString())
                //sends to ui thread to take care of?
                data.postValue(weatherdata)

                //calls weather dao insert, triggers flow that updates view, db operations happen on background threads.
                insert(weathertable)

            }
        }


    }

    @WorkerThread
    suspend fun insert(weatherTable: weatherTable){
        weatherDatabase.getDatabase(weatherApplication.applicationContext, mScope).weatherDao().insert(weatherTable)
        //pass application and then get dao from database called from context.
    }

    //sets json string for setLocation
    @WorkerThread
    suspend fun fetchAndParseWeatherData(lat: Double, lon: Double){
        val weatherDataURL = NetworkUtil.buildURLFromString(lat, lon)
        if(weatherDataURL != null){

            val jsonWeatherData = NetworkUtil.getDataFromURL(weatherDataURL)
            if(jsonWeatherData!=null){
                mJsonString = jsonWeatherData
            }
        }
    }

    //singleton pattern
    companion object{
        private var mInstance: weatherRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(weatherApplication: weatherApplication,
                        scope: CoroutineScope): weatherRepository{
            mScope = scope

            //returns mInstance if it already exists, if not creates it and stores it in mInstance and then returns it
            return mInstance?: synchronized(this){
                createDatabase(weatherApplication, scope)
            }

        }

        private fun createDatabase(weatherApplication: weatherApplication, scope: CoroutineScope) : weatherRepository{

            val instance = weatherRepository(weatherApplication, scope) //make this be a separate function?
            mInstance = instance
            return instance

        }

    }


}