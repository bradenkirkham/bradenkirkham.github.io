package com.example.bmr_app

import com.example.bmr_app.NPS_API.NationalParkData
import retrofit2.http.GET
import retrofit2.http.Query

class NationalParkAPI {

    //https://developer.nps.gov/api/v1/visitorcenters?limit=686&api_key=sXXj0N3N0JhZWuRprys6ghdPsdtod6tnFHSkr20G
    companion object{
        val DOMAIN = "https://developer.nps.gov/api/v1/"
        val API_KEY =""
    }



    interface NationalParkLoc {
        @GET("visitorcenters")
        fun getPark(@Query("api_key")key:String, @Query("limit")limit:Int): retrofit2.Call<NationalParkData>

    }




}