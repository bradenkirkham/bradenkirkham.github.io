package com.example.bmr_app.weatherData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class weatherTable(
    var id : Int,
    var city : String?,
    var lat : Double,
    var lon : Double,
    var temp : Double,
    var humidity : Double,
    var pressure : Double,
    var wind : Double,
    var desc : String?,
    var main : String?
) {

    @field:PrimaryKey(autoGenerate = true) var key: Int = 0

}