package com.example.bmr_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.bmr_app.databinding.FragmentFragWeatherBinding
import com.example.bmr_app.viewModels.LocationViewModel
import com.example.bmr_app.weatherData.*
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import androidx.lifecycle.Observer


class frag_weather : Fragment() {


    private lateinit var binding:FragmentFragWeatherBinding
    private val viewModel by activityViewModels<LocationViewModel>()
    private var mTvAvgTemp: TextView? = null

//    companion object{
//        var BaseUrl = "https://api.openweathermap.org/"
//        var AppId = ""
////        var lat = "37.445293"
////        var lon = "126.785823"
//    }

    private val mWeatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory((requireActivity().application as weatherApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        var view = inflater.inflate(R.layout.fragment_weather, container, false)

        binding = FragmentFragWeatherBinding.inflate(inflater,container,false)

        loadWeatherData(viewModel.returnLatitude()!!, viewModel.returnLogitude()!!)

        mWeatherViewModel.data.observe(viewLifecycleOwner, liveDataObserver)

        mWeatherViewModel.all.observe(viewLifecycleOwner, flowObserver)





        return binding.root
    }

    private val liveDataObserver: Observer<weatherClass> =
        Observer { weatherData ->
            weatherData.let{

                binding.currentWeatherText.text = weatherData.weather[0].description
                binding.currentTemperature.text = "${(weatherData.main.temp - 273.15).roundToInt()} 'C"
                binding.currenthumidityText.text = "${weatherData.main.humidity} %"
                binding.currentWindText.text = "${weatherData.wind.speed} KT"
                binding.currentpressuretext.text = "${weatherData.main.pressure} hPa"

                val condition:Int = weatherData.weather[0].id

                if(condition>800) binding.currentWeatherImageview.setImageResource(R.drawable.ic_cloudy)
                else if(condition == 800) binding.currentWeatherImageview.setImageResource(R.drawable.ic_clear)
                else if(condition in 701..799) binding.currentWeatherImageview.setImageResource(R.drawable.ic_random)
                else if(condition in 600 .. 699) binding.currentWeatherImageview.setImageResource(R.drawable.ic_snowy)
                else if(condition in 300 .. 599) binding.currentWeatherImageview.setImageResource(R.drawable.ic_rainy)
                else if(condition in 200 .. 250) binding.currentWeatherImageview.setImageResource(R.drawable.ic_thunder)

            }
        }

    @SuppressLint("SetTextI18n")
    private val flowObserver: Observer<Double> =
        Observer { avgTemp ->
            avgTemp.let{
                Log.d("avg", avgTemp.toString())
                mTvAvgTemp = view?.findViewById(R.id.testView)
                if(avgTemp != null) {
                    mTvAvgTemp!!.text = "Average Temperature: ${(avgTemp).roundToInt()} 'C"
                }
                else{
                    mTvAvgTemp!!.text = "Average Temperature: N/A"

                }
            }
        }

    private fun loadWeatherData(lat: Double, lon: Double){
        mWeatherViewModel.setLocation(lat, lon)
    }


//    private fun loadWeather(){
//
//        val latitude = viewModel.returnLatitude()!!.toString()
//        val longitude = viewModel.returnLogitude()!!.toString()
//
//        Log.d("--------------loadWeather latlon",latitude)
//        Log.d("--------------loadWeather latlon",longitude)
//
//
//        //Create Retrofit Builder
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BaseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val service = retrofit.create(WeatherService::class.java)
//        val call = service.getCurrentWeatherData(latitude, longitude, AppId)
//        call.enqueue(object : Callback<WeatherResponse>{
//            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
//                Log.d("MainActivity", "result :" + t.message)
//            }
//
//            override fun onResponse(
//                call: Call<WeatherResponse>,
//                response: Response<WeatherResponse>
//            ) {
//                if(response.code() == 200){
//                    val weatherResponse = response.body()
//                    Log.d("MainActivity", "result: " + weatherResponse.toString())
//                    val cTemp =  weatherResponse!!.main!!.temp - 273.15  //켈빈을 섭씨로 변환
//                    val fTemp = (cTemp*1.8)+32//Farenheit
//                    val minTemp = weatherResponse!!.main!!.temp_min - 273.15
//                    val maxTemp = weatherResponse!!.main!!.temp_max - 273.15
//                    val description = weatherResponse!!.weather!!.get(0).description
//                    val humidity = weatherResponse!!.main!!.humidity
//                    val wind = weatherResponse!!.wind!!.speed
//                    val pressure = weatherResponse!!.main!!.pressure.roundToInt()
//                    val id = weatherResponse!!.weather!!.get(0).id
//                    val stringBuilder =
//                        "지역: " + weatherResponse!!.sys!!.country + "\n" +
//                                "현재기온: " + cTemp + "\n" +
//                                "최저기온: " + minTemp + "\n" +
//                                "최고기온: " + maxTemp + "\n" +
//                                "풍속: " + weatherResponse!!.wind!!.speed+ "\n" +
//                                "일출시간: " + weatherResponse!!.sys!!.sunrise + "\n" +
//                                "일몰시간: " + weatherResponse!!.sys!!.sunset + "\n"+
//                                "아이콘: " + weatherResponse!!.weather!!.get(0).icon + "\n"+
//                                "날씨설명: " + weatherResponse!!.weather!!.get(0).description+ "\n" +
//                                "날씨 id: " + weatherResponse!!.weather!!.get(0).id
//
//
//                    val textView = view!!.findViewById<TextView>(R.id.testView)
//
//                    textView.text = stringBuilder
//
//                    dataDistributor(fTemp.roundToInt().toString(),description.toString(),humidity.toString(),wind.toString(),pressure.toString(), id)
//                }
//            }
//
//        })
//
//
//    }//fun loadWeather END

    @SuppressLint("SetTextI18n")
    private fun dataDistributor(currentTemp:String, Description:String, humidity:String, wind:String, pressure:String, id:Int){

        binding.currentWeatherText.text = Description
        binding.currentTemperature.text = "$currentTemp 'F"
        binding.currenthumidityText.text = "$humidity %"
        binding.currentWindText.text = "$wind KT"
        binding.currentpressuretext.text = "$pressure hPa"

        val condition:Int = id

        if(condition>800) binding.currentWeatherImageview.setImageResource(R.drawable.ic_cloudy)
        else if(condition == 800) binding.currentWeatherImageview.setImageResource(R.drawable.ic_clear)
        else if(condition in 701..799) binding.currentWeatherImageview.setImageResource(R.drawable.ic_random)
        else if(condition in 600 .. 699) binding.currentWeatherImageview.setImageResource(R.drawable.ic_snowy)
        else if(condition in 300 .. 599) binding.currentWeatherImageview.setImageResource(R.drawable.ic_rainy)
        else if(condition in 200 .. 250) binding.currentWeatherImageview.setImageResource(R.drawable.ic_thunder)



//    when(Description)



    }
}
interface WeatherService{

    @GET("data/2.5/weather")
    fun getCurrentWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String) :
            Call<WeatherResponse>
}

class WeatherResponse(){
    @SerializedName("weather") var weather = ArrayList<Weather>()
    @SerializedName("main") var main: Main? = null
    @SerializedName("wind") var wind : Wind? = null
    @SerializedName("sys") var sys: Sys? = null
}

class Weather {
    @SerializedName("id") var id: Int = 0
    @SerializedName("main") var main : String? = null
    @SerializedName("description") var description: String? = null
    @SerializedName("icon") var icon : String? = null
}

class Main {
    @SerializedName("temp")
    var temp: Float = 0.toFloat()
    @SerializedName("humidity")
    var humidity: Float = 0.toFloat()
    @SerializedName("pressure")
    var pressure: Float = 0.toFloat()
    @SerializedName("temp_min")
    var temp_min: Float = 0.toFloat()
    @SerializedName("temp_max")
    var temp_max: Float = 0.toFloat()

}



class Wind {
    @SerializedName("speed")
    var speed: Float = 0.toFloat()
    @SerializedName("deg")
    var deg: Float = 0.toFloat()
}

class Clouds{
    @SerializedName("all")
    var all:Int = 0
}

class Sys {
    @SerializedName("country")
    var country: String? = null
    @SerializedName("sunrise")
    var sunrise: Long = 0
    @SerializedName("sunset")
    var sunset: Long = 0
}

