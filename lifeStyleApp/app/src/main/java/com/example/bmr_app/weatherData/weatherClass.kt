package com.example.bmr_app.weatherData

data class weatherClass (val coord: Coord, val weather : List<Weather>, val main: Main, val wind: Wind, val id: Int, val name: String) {

    data class Coord(val lat : Double, val lon : Double)
    data class Weather(val id: Int, val main : String, val description : String)
    data class Main(val temp : Double, val humidity : Double, val pressure : Double)
    data class Wind(val speed : Double, val deg : Int, val gust : Double)

    //Coord
//    val lat = coord.lat
//    val lon = coord.lon

    //Weather
//    val weatherData = weather[0]
//    val con = weatherData.id
//    val mainDesc = weatherData.main
//    val description = weatherData.description

    //Main
//    val temp = main.temp
//    val humidity = main.humidity
//    val pressure = main.pressure

    //Wind
//    val speed = wind.speed
//    val deg = wind.deg
//    val gust = wind.gust

}