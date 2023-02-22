package com.example.bmr_app.weatherData

import androidx.annotation.WorkerThread
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object NetworkUtil {

    private const val Base_URL = "https://api.openweathermap.org/data/2.5/weather?lat="
    private const val lon = "&lon="
    private const val APPIDQUERY = "&appid="
    private const val app_id = ""

    fun buildURLFromString(latitude: Double, longitude: Double): URL? {
        var myURL: URL? = null

        try{
            myURL = URL(Base_URL + latitude.toString() + lon + longitude.toString()  + APPIDQUERY + app_id)
        }
        catch (e:MalformedURLException){
            e.printStackTrace()
        }
        return myURL
    }

    @WorkerThread
    fun getDataFromURL(url: URL): String?{
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val inputStream = urlConnection.inputStream

            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if(hasInput){
                scanner.next()
            }
            else{
                null
            }
        }
        finally {
            urlConnection.disconnect()
        }
    }
}